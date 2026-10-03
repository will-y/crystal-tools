package dev.willyelton.crystal.tools.common.entity.ai.behavior;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal.core.common.capability.LevelableEntity;
import dev.willyelton.crystal.tools.ModRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
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

public class TransportItemsBetweenHandlers extends Behavior<PathfinderMob> {

    private final float speedModifier;
    private final int horizontalSearchDistance;
    private final int verticalSearchDistance;
    private final Supplier<Predicate<TransportItemTarget>> sourceBlockType;
    private final Supplier<Predicate<TransportItemTarget>> destinationBlockType;
    private final Predicate<TransportItemTarget> shouldQueueForTarget;
    private final Consumer<PathfinderMob> onStartTravelling;
    private final Map<TransportItemsBetweenContainers.ContainerInteractionState, OnTargetReachedInteraction> onTargetInteractionActions;
    private @Nullable TransportItemTarget target = null;
    private TransportItemsBetweenContainers.TransportItemState state;
    private TransportItemsBetweenContainers.@Nullable ContainerInteractionState interactionState;
    private int ticksSinceReachingTarget;

    public TransportItemsBetweenHandlers(float speedModifier, Supplier<Predicate<TransportItemTarget>> sourceBlockType, Supplier<Predicate<TransportItemTarget>> destinationBlockType,
                                         int horizontalSearchDistance, int verticalSearchDistance,
                                         Map<TransportItemsBetweenContainers.ContainerInteractionState, OnTargetReachedInteraction> onTargetInteractionActions,
                                         Consumer<PathfinderMob> onStartTravelling,
                                         Predicate<TransportItemTarget> shouldQueueForTarget) {
        super(ImmutableMap.of(
                ModRegistration.VISITED_BLOCK_POSITIONS.get(),
                MemoryStatus.REGISTERED,
                ModRegistration.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get(),
                MemoryStatus.REGISTERED,
                MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS,
                MemoryStatus.VALUE_ABSENT,
                MemoryModuleType.IS_PANICKING,
                MemoryStatus.VALUE_ABSENT));
        this.speedModifier = speedModifier;
        this.sourceBlockType = sourceBlockType;
        this.destinationBlockType = destinationBlockType;
        this.horizontalSearchDistance = horizontalSearchDistance;
        this.verticalSearchDistance = verticalSearchDistance;
        this.onStartTravelling = onStartTravelling;
        this.shouldQueueForTarget = shouldQueueForTarget;
        this.onTargetInteractionActions = onTargetInteractionActions;
        this.state = TransportItemsBetweenContainers.TransportItemState.TRAVELLING;
    }

    @Override
    protected void start(ServerLevel level, PathfinderMob body, long timestamp) {
        if (body.getNavigation() instanceof GroundPathNavigation pathNavigation) {
            pathNavigation.setCanPathToTargetsBelowSurface(true);
        }
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PathfinderMob body) {
        return !body.isLeashed();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, PathfinderMob body, long timestamp) {
        return body.getBrain().getMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS).isEmpty()
                && !body.isPanicking()
                && !body.isLeashed();
    }

    @Override
    protected boolean timedOut(long timestamp) {
        return false;
    }

    @Override
    protected void tick(ServerLevel level, PathfinderMob body, long timestamp) {
        boolean updatedInvalidTarget = this.updateInvalidTarget(level, body);
        if (this.target == null) {
            this.stop(level, body, timestamp);
        } else if (!updatedInvalidTarget) {
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

    private boolean updateInvalidTarget(ServerLevel level, PathfinderMob body) {
        if (!this.hasValidTarget(level, body)) {
            this.stopTargetingCurrentTarget(body);
            Optional<TransportItemTarget> targetBlockPosition = this.getTransportTarget(level, body);

            if (targetBlockPosition.isPresent()) {
                this.target = targetBlockPosition.get();
                this.onStartTravelling(body);
                this.setVisitedBlockPos(body, level, this.target.pos, this.target.face);
            } else {
                this.enterCooldownAfterNoMatchingTargetFound(body);
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean hasValidTarget(Level level, PathfinderMob body) {
        boolean targetIsOfValidType = this.target != null && this.isWantedBlock(body, this.target) && this.targetHasNotChanged(level, this.target);
        if (targetIsOfValidType && !this.isTargetBlocked(level, this.target)) {
            if (!this.state.equals(TransportItemsBetweenContainers.TransportItemState.TRAVELLING)) {
                return true;
            }

            if (this.hasValidTravellingPath(level, this.target, body)) {
                return true;
            }

            this.markVisitedBlockPosAsUnreachable(body, level, this.target.pos, this.target.face);
        }

        return false;
    }

    private boolean isWantedBlock(PathfinderMob mob, TransportItemTarget target) {
        return canPickUpItems(mob) ? this.sourceBlockType.get().test(target) : this.destinationBlockType.get().test(target);
    }

    private boolean targetHasNotChanged(Level level, TransportItemTarget target) {
        return target.blockEntity.equals(level.getBlockEntity(target.pos));
    }

    private boolean isTargetBlocked(Level level, TransportItemTarget target) {
        return ChestBlock.isChestBlockedAt(level, target.pos);
    }

    private boolean hasValidTravellingPath(Level level, TransportItemTarget target, PathfinderMob body) {
        Path path = body.getNavigation().getPath() == null ? body.getNavigation().createPath(target.pos, 0) : body.getNavigation().getPath();
        Vec3 posFromWhichToReachTarget = this.getPositionToReachTargetFrom(path, body);
        boolean canReachTarget = this.isWithinTargetDistance(getInteractionRange(body), target, level, body, posFromWhichToReachTarget);
        boolean hasNotYetCreatedPathToTarget = path == null && !canReachTarget;
        return hasNotYetCreatedPathToTarget || this.targetIsReachableFromPosition(level, canReachTarget, posFromWhichToReachTarget, target, body);
    }

    private Vec3 getPositionToReachTargetFrom(@Nullable Path path, PathfinderMob body) {
        boolean haveNoValidPath = path == null || path.getEndNode() == null;
        Vec3 bottomCenter = haveNoValidPath ? body.position() : Vec3.atBottomCenterOf(path.getEndNode().asBlockPos());
        return this.setMiddleYPosition(body, bottomCenter);
    }

    private Vec3 setMiddleYPosition(PathfinderMob body, Vec3 pos) {
        return pos.add(0.0, body.getBoundingBox().getYsize() / 2.0, 0.0);
    }

    private boolean isWithinTargetDistance(double distance, TransportItemTarget target, Level level, PathfinderMob body, Vec3 fromPos) {
        AABB boundingBox = body.getBoundingBox();
        AABB movedBoundBox = AABB.ofSize(fromPos, boundingBox.getXsize(), boundingBox.getYsize(), boundingBox.getZsize());
        return target.state.getCollisionShape(level, target.pos).bounds().inflate(distance, 0.5, distance).move(target.pos).intersects(movedBoundBox);
    }

    private boolean targetIsReachableFromPosition(Level level, boolean canReachTarget, Vec3 pos, TransportItemTarget target, PathfinderMob body) {
        return canReachTarget && this.canSeeAnyTargetSide(target, level, body, pos);
    }

    private boolean canSeeAnyTargetSide(TransportItemTarget target, Level level, PathfinderMob body, Vec3 eyePosition) {
        Vec3 center = Vec3.atCenterOf(target.pos);
        return Direction.stream()
                .map(direction -> center.add(0.5 * direction.getStepX(), 0.5 * direction.getStepY(), 0.5 * direction.getStepZ()))
                .map(hitTarget -> level.clip(new ClipContext(eyePosition, hitTarget, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, body)))
                .anyMatch(hitResult -> hitResult.getType() == HitResult.Type.BLOCK && hitResult.getBlockPos().equals(target.pos));
    }

    private void onQueuingForTarget(TransportItemTarget target, Level level, PathfinderMob body) {
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
            TransportItemTarget connectedTarget = TransportItemTarget.tryCreatePossibleTarget(ChestBlock.getConnectedBlockPos(target.pos, target.state), level, target.face());
            return connectedTarget != null ? Stream.of(target, connectedTarget) : Stream.of(target);
        } else {
            return Stream.of(target);
        }
    }

    private void resumeTravelling(PathfinderMob body) {
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.TRAVELLING);
        this.walkTowardsTarget(body);
    }

    private void walkTowardsTarget(PathfinderMob body) {
        if (this.target != null) {
            BehaviorUtils.setWalkAndLookTargetMemories(body, this.target.pos, this.speedModifier, 0);
        }
    }

    private void setTransportingState(TransportItemsBetweenContainers.TransportItemState state) {
        this.state = state;
    }

    protected void onTravelToTarget(TransportItemTarget target, Level level, PathfinderMob body) {
        if (this.isWithinTargetDistance(3.0, target, level, body, this.getCenterPos(body)) && this.isAnotherMobInteractingWithTarget(target, level)) {
            // If it is in range but the chest is now taken, the requeue?
            this.startQueuing(body);
        } else if (this.isWithinTargetDistance(getInteractionRange(body), target, level, body, this.getCenterPos(body))) {
            // Do target action
            this.startOnReachedTargetInteraction(target, body);
        } else {
            // Keep walking
            this.walkTowardsTarget(body);
        }
    }

    private Vec3 getCenterPos(PathfinderMob body) {
        return this.setMiddleYPosition(body, body.position());
    }

    private void startQueuing(PathfinderMob body) {
        this.stopInPlace(body);
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.QUEUING);
    }

    private void stopInPlace(PathfinderMob mob) {
        mob.getNavigation().stop();
        mob.setXxa(0.0F);
        mob.setYya(0.0F);
        mob.setSpeed(0.0F);
        mob.setDeltaMovement(0.0, mob.getDeltaMovement().y, 0.0);
    }

    private void startOnReachedTargetInteraction(TransportItemTarget target, PathfinderMob body) {
        this.doReachedTargetInteraction(body, target,
                this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_ITEM),
                this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PICKUP_NO_ITEM),
                this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PLACE_ITEM),
                this.onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState.PLACE_NO_ITEM)
        );
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.INTERACTING);
    }

    private BiConsumer<PathfinderMob, ResourceHandler<ItemResource>> onReachedInteraction(TransportItemsBetweenContainers.ContainerInteractionState state) {
        return (mob, handler) -> this.setInteractionState(state);
    }

    private void doReachedTargetInteraction(PathfinderMob body, TransportItemTarget target,
                                            BiConsumer<PathfinderMob, ResourceHandler<ItemResource>> onPickupSuccess,
                                            BiConsumer<PathfinderMob, ResourceHandler<ItemResource>> onPickupFailure,
                                            BiConsumer<PathfinderMob, ResourceHandler<ItemResource>> onPlaceSuccess,
                                            BiConsumer<PathfinderMob, ResourceHandler<ItemResource>> onPlaceFailure) {
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
    private static boolean matchesLeavingItemsRequirement(PathfinderMob body, ResourceHandler<ItemResource> handler) {
        return ResourceHandlerUtil.isEmpty(handler) || hasItemMatchingHandItem(body, handler);
    }

    // TODO: Match all items, not just hand?
    private static boolean hasItemMatchingHandItem(PathfinderMob body, ResourceHandler<ItemResource> handler) {
        ItemStack mainHandItem = body.getMainHandItem();

        return ResourceHandlerUtil.contains(handler, ItemResource.of(mainHandItem));
    }

    private static boolean canPickUpItems(PathfinderMob body) {
        // TODO: Mob handler to check if there is space
        return body.getMainHandItem().isEmpty();
    }

    private static double getInteractionRange(PathfinderMob body) {
        // TODO: Could increase this
        return body.getNavigation().getPath() != null && body.getNavigation().getPath().isDone() ? 1.0 : 0.5;
    }

    private void setInteractionState(TransportItemsBetweenContainers.ContainerInteractionState state) {
        this.interactionState = state;
    }

    private void onReachedTarget(TransportItemTarget target, Level level, PathfinderMob body) {
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

    private void onStartTravelling(PathfinderMob body) {
        this.onStartTravelling.accept(body);
        this.setTransportingState(TransportItemsBetweenContainers.TransportItemState.TRAVELLING);
        this.interactionState = null;
        this.ticksSinceReachingTarget = 0;
    }

    private void onTargetInteraction(TransportItemTarget target, PathfinderMob body) {
        body.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(target.pos));
        this.stopInPlace(body);
        if (this.interactionState != null) {
            Optional.ofNullable(this.onTargetInteractionActions.get(this.interactionState))
                    .ifPresent(action -> action.accept(body, target, this.ticksSinceReachingTarget));
        }
    }

    // TODO: All Items
    private void pickUpItems(PathfinderMob body, ResourceHandler<ItemResource> handler) {
        body.setItemSlot(EquipmentSlot.MAINHAND, pickupItemFromContainer(handler));
        body.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        this.clearMemoriesAfterMatchingTargetFound(body);
    }

    private static ItemStack pickupItemFromContainer(ResourceHandler<ItemResource> handler) {
        try (Transaction tx = Transaction.openRoot()) {
            // TODO: Increase stack size
            ResourceStack<ItemResource> result = ResourceHandlerUtil.extractFirst(handler, Predicates.alwaysTrue(), 16, tx);
            tx.commit();
            if (result != null && !result.isEmpty()) {
                return result.resource().toStack(result.amount());
            }
        }

        return ItemStack.EMPTY;
    }

    protected void clearMemoriesAfterMatchingTargetFound(PathfinderMob body) {
        this.stopTargetingCurrentTarget(body);
        body.getBrain().eraseMemory(ModRegistration.VISITED_BLOCK_POSITIONS.get());
        body.getBrain().eraseMemory(ModRegistration.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get());
    }

    protected void stopTargetingCurrentTarget(PathfinderMob body) {
        this.ticksSinceReachingTarget = 0;
        this.target = null;
        body.getNavigation().stop();
        body.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    private void putDownItem(PathfinderMob body, ResourceHandler<ItemResource> handler) {
        LevelableEntity levelableEntity = LevelableEntity.of(body, body.level().registryAccess());

        ItemStack itemsLeftAfterVisitingChest = addItemsToContainer(body, handler, levelableEntity);

        body.setItemSlot(EquipmentSlot.MAINHAND, itemsLeftAfterVisitingChest);
        if (itemsLeftAfterVisitingChest.isEmpty()) {
            this.clearMemoriesAfterMatchingTargetFound(body);
        } else {
            this.stopTargetingCurrentTarget(body);
        }
    }

    private static ItemStack addItemsToContainer(PathfinderMob body, ResourceHandler<ItemResource> handler, @Nullable LevelableEntity levelableEntity) {
        // TODO: More items
        ItemStack itemStack = body.getMainHandItem();
        int inserted = ResourceHandlerUtil.insertStacking(handler, ItemResource.of(itemStack), itemStack.count(), null);

        if (levelableEntity != null) {
            levelableEntity.addExp(body.level(), body.getOnPos(), body, inserted);
        }

        if (inserted == itemStack.count()) {
            return ItemStack.EMPTY;
        } else {
            return itemStack.copyWithCount(itemStack.count() - inserted);
        }
    }

    private Optional<TransportItemTarget> getTransportTarget(ServerLevel level, PathfinderMob body) {
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
                        // TODO: Loop over directions here
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

    private AABB getTargetSearchArea(PathfinderMob mob) {
        int horizontalSearchDistance = this.getHorizontalSearchDistance(mob);
        return new AABB(mob.blockPosition()).inflate(horizontalSearchDistance, this.getVerticalSearchDistance(mob), horizontalSearchDistance);
    }

    private int getHorizontalSearchDistance(PathfinderMob mob) {
        return mob.isPassenger() ? 1 : this.horizontalSearchDistance;
    }

    private int getVerticalSearchDistance(PathfinderMob mob) {
        return mob.isPassenger() ? 1 : this.verticalSearchDistance;
    }

    private static Set<GlobalPosDirection> getVisitedPositions(PathfinderMob mob) {
        return mob.getBrain().getMemory(ModRegistration.VISITED_BLOCK_POSITIONS.get()).orElse(Set.of());
    }

    private static Set<GlobalPosDirection> getUnreachablePositions(PathfinderMob mob) {
        return mob.getBrain().getMemory(ModRegistration.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get()).orElse(Set.of());
    }

    private @Nullable TransportItemTarget isTargetValidToPick(PathfinderMob body, Level level,
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
                .map(transportItemTarget -> new GlobalPosDirection(level.dimension(), transportItemTarget.pos, transportItemTarget.face))
                .anyMatch(pos -> visitedPositions.contains(pos) || unreachablePositions.contains(pos));
    }

    private boolean isContainerLocked(TransportItemTarget transportItemTarget) {
        return transportItemTarget.blockEntity instanceof BaseContainerBlockEntity blockEntity && blockEntity.isLocked();
    }

    protected void setVisitedBlockPos(PathfinderMob body, Level level, BlockPos target, Direction face) {
        Set<GlobalPosDirection> visitedPositions = new HashSet<>(getVisitedPositions(body));
        visitedPositions.add(new GlobalPosDirection(level.dimension(), target, face));
        if (visitedPositions.size() > 10) {
            this.enterCooldownAfterNoMatchingTargetFound(body);
        } else {
            body.getBrain().setMemoryWithExpiry(ModRegistration.VISITED_BLOCK_POSITIONS.get(), visitedPositions, 6000L);
        }
    }

    private void enterCooldownAfterNoMatchingTargetFound(PathfinderMob body) {
        this.stopTargetingCurrentTarget(body);
        // TODO: Something to increase?
        body.getBrain().setMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS, 140);
        body.getBrain().eraseMemory(ModRegistration.VISITED_BLOCK_POSITIONS.get());
        body.getBrain().eraseMemory(ModRegistration.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get());
    }

    protected void markVisitedBlockPosAsUnreachable(PathfinderMob body, Level level, BlockPos target, Direction face) {
        Set<GlobalPosDirection> visitedPositions = new HashSet<>(getVisitedPositions(body));
        visitedPositions.remove(new GlobalPosDirection(level.dimension(), target, face));
        Set<GlobalPosDirection> unreachablePositions = new HashSet<>(getUnreachablePositions(body));
        unreachablePositions.add(new GlobalPosDirection(level.dimension(), target, face));
        if (unreachablePositions.size() > 50) {
            this.enterCooldownAfterNoMatchingTargetFound(body);
        } else {
            body.getBrain().setMemoryWithExpiry(ModRegistration.VISITED_BLOCK_POSITIONS.get(), visitedPositions, 6000L);
            body.getBrain().setMemoryWithExpiry(ModRegistration.UNREACHABLE_TRANSPORT_BLOCK_POSITIONS.get(), unreachablePositions, 6000L);
        }
    }


    @FunctionalInterface
    public interface OnTargetReachedInteraction extends TriConsumer<PathfinderMob, TransportItemTarget, Integer> {
    }

    // TODO: Face somehow
    public record TransportItemTarget(BlockPos pos, ResourceHandler<ItemResource> handler, BlockEntity blockEntity,
                                      BlockState state, Direction face) {
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
