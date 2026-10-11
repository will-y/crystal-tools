package dev.willyelton.crystal.tools.common.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import dev.willyelton.crystal.core.common.capability.LevelableEntity;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.entity.CrystalGolem;
import dev.willyelton.crystal.tools.utils.constants.EntitySkills;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PickUpItems extends CrystalGolemBehavior<GlobalPos> {

    private ItemPickupTarget target;

    public PickUpItems(float speedModifier, int horizontalSearchDistance, int verticalSearchDistance) {
        super(ImmutableMap.of(
                MemoryModuleType.VISITED_BLOCK_POSITIONS,
                MemoryStatus.REGISTERED,
                MemoryModuleType.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS,
                MemoryStatus.REGISTERED,
                ModRegistration.PICKUP_ITEMS_COOLDOWN_TICKS.get(),
                MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.IS_PANICKING,
                MemoryStatus.VALUE_ABSENT), speedModifier, horizontalSearchDistance, verticalSearchDistance);
    }

    @Override
    protected void start(ServerLevel level, CrystalGolem body, long timestamp) {
        if (body.getNavigation() instanceof GroundPathNavigation pathNavigation) {
            pathNavigation.setCanPathToTargetsBelowSurface(true);
        }
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, CrystalGolem body) {
        LevelableEntity levelable = LevelableEntity.of(body, level.registryAccess());
        return !body.isLeashed() && levelable != null && levelable.getEntitySkillData().hasSkill(EntitySkills.ITEM_PICKUP);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, CrystalGolem body, long timestamp) {
        return body.getBrain().getMemory(cooldownMemoryModule()).isEmpty()
                && !body.isPanicking()
                && !body.isLeashed();
    }

    @Override
    protected boolean timedOut(long timestamp) {
        return false;
    }

    @Override
    protected void tick(ServerLevel level, CrystalGolem body, long timestamp) {
        boolean updatedInvalidTarget = this.updateInvalidTarget(level, body);
        if (this.target == null) {
            this.stop(level, body, timestamp);
        } else if (updatedInvalidTarget) {
            this.onTravelToTarget(this.target, level, body);
        }
    }

    @Override
    protected void stop(ServerLevel level, CrystalGolem body, long timestamp) {
        this.onStartTravelling(body);
        if (body.getNavigation() instanceof GroundPathNavigation pathNavigation) {
            pathNavigation.setCanPathToTargetsBelowSurface(false);
        }
    }

    /// Returns true if this has a valid target
    private boolean updateInvalidTarget(ServerLevel level, CrystalGolem body) {
        if (!this.hasValidTarget(level, body)) {
            this.stopTargetingCurrentTarget(body);
            Optional<ItemPickupTarget> target = this.getPickupTarget(level, body);

            if (target.isPresent()) {
                this.target = target.get();
                this.onStartTravelling(body);
                this.setVisitedBlockPos(body, new GlobalPos(level.dimension(), this.target.blockPos()));
            } else {
                this.enterCooldownAfterNoMatchingTargetFound(body);
            }

            return false;
        } else {
            return true;
        }
    }

    private void onTravelToTarget(ItemPickupTarget target, Level level, CrystalGolem body) {
        if (this.isWithinTargetDistance(3.0, target, level, body, this.getCenterPos(body))) {
            // Do target action
            pickUpItem(level, body);
        } else {
            this.walkTowardsTarget(body, this.target);
        }
    }

    private void pickUpItem(Level level, CrystalGolem body) {
        // Check if it is still there
        List<ItemEntity> matchingEntities = level.getEntitiesOfClass(ItemEntity.class, body.getHitbox().inflate(3.0F),
                e -> e instanceof ItemEntity itemEntity && itemEntity.getItem() == this.target.stack);

        if (!matchingEntities.isEmpty()) {
            ItemEntity itemEntity = matchingEntities.getFirst();
            ItemStack stack = itemEntity.getItem();
            ItemStack newStack = body.addToInventory(stack);

            if (newStack.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.setItem(newStack);
            }
        }

        this.stopTargetingCurrentTarget(body);
    }

    private boolean hasValidTarget(Level level, CrystalGolem body) {
        if (this.target != null && !target.stack().isEmpty() && this.canPickupItem(body, target.stack())) {
            if (this.hasValidTravellingPath(level, this.target, body)) {
                return true;
            }

            this.markVisitedBlockPosAsUnreachable(body, level, this.target.blockPos());
        }

        return false;
    }

    private void markVisitedBlockPosAsUnreachable(CrystalGolem body, Level level, BlockPos target) {
        Set<GlobalPos> visitedPositions = new HashSet<>(getVisitedPositions(body));
        visitedPositions.remove(new GlobalPos(level.dimension(), target));
        Set<GlobalPos> unreachablePositions = new HashSet<>(getUnreachablePositions(body));
        unreachablePositions.add(new GlobalPos(level.dimension(), target));
        if (unreachablePositions.size() > 50) {
            this.enterCooldownAfterNoMatchingTargetFound(body);
        } else {
            body.getBrain().setMemoryWithExpiry(visitedMemoryModule(), visitedPositions, 6000L);
            body.getBrain().setMemoryWithExpiry(unreachableMemoryModule(), unreachablePositions, 6000L);
        }
    }

    private Optional<ItemPickupTarget> getPickupTarget(ServerLevel level, CrystalGolem body) {
        AABB targetBlockSearchArea = this.getTargetSearchArea(body);

        List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, targetBlockSearchArea)
                .stream()
                .sorted(Comparator.comparingDouble(e -> e.distanceTo(body)))
                .toList();

        for (ItemEntity itemEntity : itemEntities) {
            if (canPickupItem(body, itemEntity.getItem())) {
                BlockPos pos = itemEntity.getOnPos();
                if (getVisitedPositions(body).contains(new GlobalPos(level.dimension(), pos)) ||
                        getUnreachablePositions(body).contains(new GlobalPos(level.dimension(), pos))) {
                    continue;
                }

                return Optional.of(new ItemPickupTarget(new GlobalPos(level.dimension(), itemEntity.getOnPos()), itemEntity.getItem()));
            }
        }

        return Optional.empty();
    }

    private boolean canPickupItem(CrystalGolem body, ItemStack stack) {
        return body.canAcceptStack(stack);
    }

    private void onStartTravelling(CrystalGolem body) {
        body.clearOpenedChestPos();
        body.setState(CopperGolemState.IDLE);
    }

    @Override
    protected MemoryModuleType<Set<GlobalPos>> visitedMemoryModule() {
        return MemoryModuleType.VISITED_BLOCK_POSITIONS;
    }

    @Override
    protected MemoryModuleType<Set<GlobalPos>> unreachableMemoryModule() {
        return MemoryModuleType.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS;
    }

    @Override
    protected MemoryModuleType<Integer> cooldownMemoryModule() {
        return ModRegistration.PICKUP_ITEMS_COOLDOWN_TICKS.get();
    }

    @Override
    protected void stopTargetingCurrentTarget(CrystalGolem body) {
        this.target = null;
        body.getNavigation().stop();
        body.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    private record ItemPickupTarget(GlobalPos pos, ItemStack stack) implements CrystalGolemTarget {
        @Override
        public BlockPos blockPos() {
            return pos.pos();
        }

        @Override
        public AABB bounds(Level level) {
            return Shapes.block().bounds();
        }
    }
}
