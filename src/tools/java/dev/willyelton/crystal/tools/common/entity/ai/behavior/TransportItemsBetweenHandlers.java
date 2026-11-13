package dev.willyelton.crystal.tools.common.entity.ai.behavior;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal.core.common.capability.LevelableEntity;
import dev.willyelton.crystal.core.utils.TransferUtils;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.entity.CrystalGolem;
import dev.willyelton.crystal.tools.common.inventory.CrystalGolemItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.TransportItemsBetweenContainers;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.commons.lang3.function.TriConsumer;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TransportItemsBetweenHandlers extends CrystalGolemBehavior<TransportItemsBetweenHandlers.GlobalPosDirection> {

    private final Supplier<Predicate<TransportItemTarget>> sourceBlockType;
    private final Supplier<Predicate<TransportItemTarget>> destinationBlockType;
    private final Predicate<TransportItemTarget> shouldQueueForTarget;
    private final Consumer<CrystalGolem> onStartTravelling;
    private final Map<TransportItemsBetweenContainers.ContainerInteractionState, OnTargetReachedInteraction> onTargetInteractionActions;
    private @Nullable TransportItemTarget target = null;
    private TransportItemsBetweenContainers.TransportItemState state;
    private TransportItemsBetweenContainers.@Nullable ContainerInteractionState interactionState;
    private int ticksSinceReachingTarget;

    public TransportItemsBetweenHandlers(float speedModifier, Supplier<Predicate<TransportItemTarget>> sourceBlockType, Supplier<Predicate<TransportItemTarget>> destinationBlockType,
                                         int horizontalSearchDistance, int verticalSearchDistance,
                                         Map<TransportItemsBetweenContainers.ContainerInteractionState, OnTargetReachedInteraction> onTargetInteractionActions,
                                         Consumer<CrystalGolem> onStartTravelling,
                                         Predicate<TransportItemTarget> shouldQueueForTarget) {
        super(ImmutableMap.of(
                ModRegistration.VISITED_BLOCK_POSITIONS.get(),
                MemoryStatus.REGISTERED,
                ModRegistration.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get(),
                MemoryStatus.REGISTERED,
                MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS,
                MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.IS_PANICKING,
                MemoryStatus.VALUE_ABSENT), speedModifier, horizontalSearchDistance, verticalSearchDistance);
        this.sourceBlockType = sourceBlockType;
        this.destinationBlockType = destinationBlockType;
        this.onStartTravelling = onStartTravelling;
        this.shouldQueueForTarget = shouldQueueForTarget;
        this.onTargetInteractionActions = onTargetInteractionActions;
        this.state = TransportItemsBetweenContainers.TransportItemState.TRAVELLING;
    }

    @Override
    protected void start(ServerLevel level, CrystalGolem body, long timestamp) {
        if (body.getNavigation() instanceof GroundPathNavigation pathNavigation) {
            pathNavigation.setCanPathToTargetsBelowSurface(true);
        }
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, CrystalGolem body) {
        return !body.isLeashed();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, CrystalGolem body, long timestamp) {
        return body.getBrain().getMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS).isEmpty()
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
            if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.QUEUING)) {
                this.onQueuingForTarget(this.target, level, body);
            }

            if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.TRAVELLING)) {
                this.onTravelToTarget(this.target, level, body);
            }

            if (this.state.equals(TransportItemsBetweenContainers.TransportItemState.INTERACTING)) {
                this.onReachedTarget(this.target, level, body);
            }
        }
    }

    @Override
    protected void stop(ServerLevel level, CrystalGolem body, long timestamp) {
        this.onStartTravelling(body);
        if (body.getNavigation() instanceof GroundPathNavigation pathNavigation) {
            pathNavigation.setCanPathToTargetsBelowSurface(false);
        }
    }

    private boolean updateInvalidTarget(ServerLevel level, CrystalGolem body) {
        if (!this.hasValidTarget(level, body)) {
            this.stopTargetingCurrentTarget(body);
            Optional<TransportItemTarget> targetBlockPosition = this.getTransportTarget(level, body);

            if (targetBlockPosition.isPresent()) {
                this.target = targetBlockPosition.get();
                this.onStartTravelling(body);
                this.setVisitedBlockPos(body, new GlobalPosDirection(level.dimension(), this.target.blockPos(), this.target.face()));
            } else {
                this.enterCooldownAfterNoMatchingTargetFound(body);
            }

            return false;
        } else {
            return true;
        }
    }

    private boolean hasValidTarget(Level level, CrystalGolem body) {
        boolean targetIsOfValidType = this.target != null && this.isWantedBlock(body, this.target) && this.targetHasNotChanged(level, this.target);
        if (targetIsOfValidType && !this.isTargetBlocked(level, this.target)) {
            if (!this.state.equals(TransportItemsBetweenContainers.TransportItemState.TRAVELLING)) {
                return true;
            }

            if (this.hasValidTravellingPath(level, this.target, body)) {
                return true;
            }

            this.markVisitedBlockPosAsUnreachable(body, level, this.target.blockPos, this.target.face);
        }

        return false;
    }

    private boolean isWantedBlock(CrystalGolem mob, TransportItemTarget target) {
        return canPickUpItems(mob, target) ? this.sourceBlockType.get().test(target) : this.destinationBlockType.get().test(target);
    }

    private boolean targetHasNotChanged(Level level, TransportItemTarget target) {
        return target.blockEntity.equals(level.getBlockEntity(target.blockPos));
    }

    private boolean isTargetBlocked(Level level, TransportItemTarget target) {
        return ChestBlock.isChestBlockedAt(level, target.blockPos);
    }

    private void onQueuingForTarget(TransportItemTarget target, Level level, CrystalGolem body) {
        if (!this.isAnotherMobInteractingWithTarget(target, level)) {
            this.resumeTravelling(body);
        }
    }

    // Passed in from AI, if something else has the chest open, do not queue
    private boolean isAnotherMobInteractingWithTarget(TransportItemTarget target, Level level) {
        return this.getConnectedTargets(target, level).anyMatch(this.shouldQueueForTarget);
    }

    // Gets all chests if double chest
    private Stream<TransportItemTarget> getConnectedTargets(TransportItemTarget target, Level level) {
        if (target.state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE) != ChestType.SINGLE) {
            TransportItemTarget connectedTarget = TransportItemTarget.tryCreatePossibleTarget(ChestBlock.getConnectedBlockPos(target.blockPos, target.state), level, target.face());
            return connectedTarget != null ? Stream.of(target, connectedTarget) : Stream.of(target);
        } else {
            return Stream.of(target);
        }
    }

    private void resumeTravelling(CrystalGolem body) {
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.TRAVELLING);
        this.walkTowardsTarget(body, this.target);
    }

    private void setTransportingState(TransportItemsBetweenContainers.TransportItemState state) {
        this.state = state;
    }

    protected void onTravelToTarget(TransportItemTarget target, Level level, CrystalGolem body) {
        if (this.isWithinTargetDistance(3.0, target, level, body, this.getCenterPos(body)) && this.isAnotherMobInteractingWithTarget(target, level)) {
            // If it is in range but the chest is now taken, then requeue?
            this.startQueuing(body);
        } else if (this.isWithinTargetDistance(body.getInteractionRange(), target, level, body, this.getCenterPos(body))) {
            // Do target action
            this.startOnReachedTargetInteraction(target, body);
        } else {
            // Keep walking
            this.walkTowardsTarget(body, this.target);
        }
    }

    private void startQueuing(CrystalGolem body) {
        this.stopInPlace(body);
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.QUEUING);
    }

    private void stopInPlace(CrystalGolem mob) {
        mob.getNavigation().stop();
        mob.setXxa(0.0F);
        mob.setYya(0.0F);
        mob.setSpeed(0.0F);
        mob.setDeltaMovement(0.0, mob.getDeltaMovement().y, 0.0);
    }

    private void startOnReachedTargetInteraction(TransportItemTarget target, CrystalGolem body) {
        this.doReachedTargetInteraction(body, target,
                this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_ITEM),
                this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_NO_ITEM),
                this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PLACE_ITEM),
                this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PLACE_NO_ITEM)
        );
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.INTERACTING);
    }

    private BiConsumer<CrystalGolem, ResourceHandler<ItemResource>> onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState state) {
        return (mob, handler) -> this.setInteractionState(state);
    }

    private void doReachedTargetInteraction(CrystalGolem body, TransportItemTarget target,
                                            BiConsumer<CrystalGolem, ResourceHandler<ItemResource>> onPickupSuccess,
                                            BiConsumer<CrystalGolem, ResourceHandler<ItemResource>> onPickupFailure,
                                            BiConsumer<CrystalGolem, ResourceHandler<ItemResource>> onPlaceSuccess,
                                            BiConsumer<CrystalGolem, ResourceHandler<ItemResource>> onPlaceFailure) {
        if (canPickUpItems(body)) {
            if (matchesGettingItemsRequirement(target.handler)) {
                onPickupSuccess.accept(body, target.handler);
            } else {
                onPickupFailure.accept(body, target.handler);
            }
        } else if (matchesLeavingItemsRequirement(body, target.handler)) {
            onPlaceSuccess.accept(body, target.handler);
        } else {
            onPlaceFailure.accept(body, target.handler);
        }
    }

    private static boolean matchesGettingItemsRequirement(ResourceHandler<ItemResource> handler) {
        return !ResourceHandlerUtil.isEmpty(handler);
    }

    // TODO: If specific blocks are chosen, don't care about matching items
    private static boolean matchesLeavingItemsRequirement(CrystalGolem body, ResourceHandler<ItemResource> handler) {
        return ResourceHandlerUtil.isEmpty(handler) || hasItemMatchingHandItem(body, handler);
    }

    // TODO: Match all items, not just hand?
    private static boolean hasItemMatchingHandItem(CrystalGolem body, ResourceHandler<ItemResource> handler) {
        ItemStack mainHandItem = body.getMainHandItem();

        return ResourceHandlerUtil.contains(handler, ItemResource.of(mainHandItem));
    }

    private static boolean canPickUpItems(CrystalGolem body) {
        return !body.getInventoryCapability().isFull();
    }

    private void setInteractionState(TransportItemsBetweenContainers.ContainerInteractionState state) {
        this.interactionState = state;
    }

    private void onReachedTarget(TransportItemTarget target, Level level, CrystalGolem body) {
        if (!this.isWithinTargetDistance(2.0, target, level, body, this.getCenterPos(body))) {
            this.onStartTravelling(body);
        } else {
            this.ticksSinceReachingTarget++;
            this.onTargetInteraction(target, body);
            if (this.ticksSinceReachingTarget >= 60) {
                this.doReachedTargetInteraction(
                        body,
                        target,
                        this::pickUpItems,
                        (mob, container) -> this.stopTargetingCurrentTarget(body),
                        this::putDownItem,
                        (mob, container) -> this.stopTargetingCurrentTarget(body)
                );
                this.onStartTravelling(body);
            }
        }
    }

    private void onStartTravelling(CrystalGolem body) {
        this.onStartTravelling.accept(body);
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.TRAVELLING);
        this.interactionState = null;
        this.ticksSinceReachingTarget = 0;
    }

    private void onTargetInteraction(TransportItemTarget target, CrystalGolem body) {
        body.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(target.blockPos));
        this.stopInPlace(body);
        if (this.interactionState != null) {
            Optional.ofNullable(this.onTargetInteractionActions.get(this.interactionState))
                    .ifPresent(action -> action.accept(body, target, this.ticksSinceReachingTarget));
        }
    }

    private void pickUpItems(CrystalGolem body, ResourceHandler<ItemResource> handler) {
        TransferUtils.moveAll(handler, body.getInventoryCapability(), Predicates.alwaysTrue());
        this.clearMemoriesAfterMatchingTargetFound(body);
    }

    protected void clearMemoriesAfterMatchingTargetFound(CrystalGolem body) {
        this.stopTargetingCurrentTarget(body);
        body.getBrain().eraseMemory(ModRegistration.VISITED_BLOCK_POSITIONS.get());
        body.getBrain().eraseMemory(ModRegistration.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get());
    }

    @Override
    public void stopTargetingCurrentTarget(CrystalGolem body) {
        this.ticksSinceReachingTarget = 0;
        this.target = null;
        body.getNavigation().stop();
        body.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    private void putDownItem(CrystalGolem body, ResourceHandler<ItemResource> handler) {
        LevelableEntity levelableEntity = LevelableEntity.of(body, body.level().registryAccess());

        boolean itemsLeftAfterVisitingChest = addItemsToContainer(body, handler, levelableEntity);

        // TODO: Not sure about this if there are leftovers
        if (itemsLeftAfterVisitingChest) {
            this.clearMemoriesAfterMatchingTargetFound(body);
        } else {
            this.stopTargetingCurrentTarget(body);
        }
    }

    private static boolean addItemsToContainer(CrystalGolem body, ResourceHandler<ItemResource> handler, @Nullable LevelableEntity levelableEntity) {
        CrystalGolemItemStackHandler bodyHandler = body.getInventoryCapability();
        int inserted = TransferUtils.moveAll(bodyHandler, handler, resource -> ResourceHandlerUtil.contains(handler, resource));

        if (levelableEntity != null) {
            levelableEntity.addExp(body.level(), body.getOnPos(), body, inserted);
        }

        return !bodyHandler.isEmpty();
    }

    private Optional<TransportItemTarget> getTransportTarget(ServerLevel level, CrystalGolem body) {
        AABB targetBlockSearchArea = this.getTargetSearchArea(body);
        Set<GlobalPosDirection> visitedPositions = getVisitedPositions(body);
        Set<GlobalPosDirection> unreachablePositions = getUnreachablePositions(body);
        List<ChunkPos> list = ChunkPos.rangeClosed(ChunkPos.containing(body.blockPosition()), Math.floorDiv(this.getHorizontalSearchDistance(body), 16) + 1)
                .toList();
        TransportItemTarget target = null;
        double closestDistance = Float.MAX_VALUE;

        for (ChunkPos chunkPos : list) {
            LevelChunk levelChunk = level.getChunkSource().getChunkNow(chunkPos.x(), chunkPos.z());
            if (levelChunk != null) {
                for (BlockEntity potentialTarget : levelChunk.getBlockEntities().values()) {
                    double distance = potentialTarget.getBlockPos().distToCenterSqr(body.position());
                    if (distance < closestDistance) {
                        for (Direction direction : Direction.values()) {
                            TransportItemTarget targetValidToPick = this.isTargetValidToPick(body, level, potentialTarget, visitedPositions, unreachablePositions, targetBlockSearchArea, direction);

                            if (targetValidToPick != null) {
                                target = targetValidToPick;
                                closestDistance = distance;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return target == null ? Optional.empty() : Optional.of(target);
    }

    private @Nullable TransportItemTarget isTargetValidToPick(CrystalGolem body, Level level,
                                                              BlockEntity blockEntity, Set<GlobalPosDirection> visitedPositions,
                                                              Set<GlobalPosDirection> unreachablePositions,
                                                              AABB targetBlockSearchArea, Direction face) {
        BlockPos blockPos = blockEntity.getBlockPos();
        boolean isWithinSearchArea = targetBlockSearchArea.contains(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (!isWithinSearchArea) {
            return null;
        }

        TransportItemTarget transportItemTarget = TransportItemTarget.tryCreatePossibleTarget(blockEntity, level, face);
        if (transportItemTarget == null) {
            return null;
        }

        boolean isValidTarget = this.isWantedBlock(body, transportItemTarget)
                && !this.isPositionAlreadyVisited(visitedPositions, unreachablePositions, transportItemTarget, level)
                && !this.isContainerLocked(transportItemTarget);
        return isValidTarget ? transportItemTarget : null;
    }

    private boolean isPositionAlreadyVisited(Set<GlobalPosDirection> visitedPositions, Set<GlobalPosDirection> unreachablePositions,
                                             TransportItemTarget target, Level level) {
        return this.getConnectedTargets(target, level)
                .map(transportItemTarget -> new GlobalPosDirection(level.dimension(), transportItemTarget.blockPos, transportItemTarget.face))
                .anyMatch(pos -> visitedPositions.contains(pos) || unreachablePositions.contains(pos));
    }

    private boolean isContainerLocked(TransportItemTarget transportItemTarget) {
        return transportItemTarget.blockEntity instanceof BaseContainerBlockEntity blockEntity && blockEntity.isLocked();
    }

    protected void markVisitedBlockPosAsUnreachable(CrystalGolem body, Level level, BlockPos target, Direction face) {
        Set<GlobalPosDirection> visitedPositions = new HashSet<>(getVisitedPositions(body));
        visitedPositions.remove(new GlobalPosDirection(level.dimension(), target, face));
        Set<GlobalPosDirection> unreachablePositions = new HashSet<>(getUnreachablePositions(body));
        unreachablePositions.add(new GlobalPosDirection(level.dimension(), target, face));
        if (unreachablePositions.size() > 50) {
            this.enterCooldownAfterNoMatchingTargetFound(body);
        } else {
            body.getBrain().setMemoryWithExpiry(visitedMemoryModule(), visitedPositions, 6000L);
            body.getBrain().setMemoryWithExpiry(unreachableMemoryModule(), unreachablePositions, 6000L);
        }
    }

    @Override
    public MemoryModuleType<Set<GlobalPosDirection>> visitedMemoryModule() {
        return ModRegistration.VISITED_BLOCK_POSITIONS.get();
    }

    @Override
    public MemoryModuleType<Set<GlobalPosDirection>> unreachableMemoryModule() {
        return ModRegistration.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get();
    }

    @Override
    public MemoryModuleType<Integer> cooldownMemoryModule() {
        return MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS;
    }

    @FunctionalInterface
    public interface OnTargetReachedInteraction extends TriConsumer<CrystalGolem, TransportItemTarget, Integer> {
    }

    public record TransportItemTarget(BlockPos blockPos, ResourceHandler<ItemResource> handler, BlockEntity blockEntity,
                                      BlockState state, Direction face) implements CrystalGolemTarget {
        public static @Nullable TransportItemTarget tryCreatePossibleTarget(BlockEntity blockEntity, Level level, Direction face) {
            BlockPos blockPos = blockEntity.getBlockPos();
            BlockState blockState = blockEntity.getBlockState();
            ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, blockPos, face);

            return handler != null ? new TransportItemTarget(blockPos, handler, blockEntity, blockState, face) : null;
        }

        public static @Nullable TransportItemTarget tryCreatePossibleTarget(BlockPos blockPos, Level level, Direction face) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            return blockEntity == null ? null : tryCreatePossibleTarget(blockEntity, level, face);
        }

        @Override
        public AABB bounds(Level level) {
            return state.getCollisionShape(level, blockPos).bounds();
        }
    }

    public record GlobalPosDirection(ResourceKey<Level> dimension, BlockPos pos, Direction face) {
        // codec
        public static final Codec<GlobalPosDirection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(GlobalPosDirection::dimension),
                BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPosDirection::pos),
                Direction.CODEC.fieldOf("face").forGetter(GlobalPosDirection::face)
        ).apply(instance, GlobalPosDirection::new));
    }
}
