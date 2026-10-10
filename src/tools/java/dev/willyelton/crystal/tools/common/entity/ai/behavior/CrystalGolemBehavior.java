package dev.willyelton.crystal.tools.common.entity.ai.behavior;

import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.entity.CrystalGolem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class CrystalGolemBehavior<T> extends Behavior<CrystalGolem>  {

    protected final float speedModifier;
    protected final int horizontalSearchDistance;
    protected final int verticalSearchDistance;

    public CrystalGolemBehavior(Map<MemoryModuleType<?>, MemoryStatus> entryCondition, float speedModifier,
                                int horizontalSearchDistance, int verticalSearchDistance) {
        super(entryCondition);
        this.speedModifier = speedModifier;
        this.horizontalSearchDistance = horizontalSearchDistance;
        this.verticalSearchDistance = verticalSearchDistance;
    }

    protected boolean hasValidTravellingPath(Level level, CrystalGolemTarget target, CrystalGolem body) {
        Path path = body.getNavigation().getPath() == null ? body.getNavigation().createPath(target.blockPos(), 0) : body.getNavigation().getPath();
        Vec3 posFromWhichToReachTarget = this.getPositionToReachTargetFrom(path, body);
        boolean canReachTarget = this.isWithinTargetDistance(body.getInteractionRange(), target, level, body, posFromWhichToReachTarget);
        boolean hasNotYetCreatedPathToTarget = path == null && !canReachTarget;
        return hasNotYetCreatedPathToTarget || this.targetIsReachableFromPosition(level, canReachTarget, posFromWhichToReachTarget, target, body);
    }

    protected Vec3 getPositionToReachTargetFrom(@Nullable Path path, PathfinderMob body) {
        boolean haveNoValidPath = path == null || path.getEndNode() == null;
        Vec3 bottomCenter = haveNoValidPath ? body.position() : Vec3.atBottomCenterOf(path.getEndNode().asBlockPos());
        return this.setMiddleYPosition(body, bottomCenter);
    }

    protected Vec3 setMiddleYPosition(PathfinderMob body, Vec3 pos) {
        return pos.add(0.0, body.getBoundingBox().getYsize() / 2.0, 0.0);
    }

    protected boolean isWithinTargetDistance(double distance, CrystalGolemTarget target, Level level, PathfinderMob body, Vec3 fromPos) {
        AABB boundingBox = body.getBoundingBox();
        AABB movedBoundBox = AABB.ofSize(fromPos, boundingBox.getXsize(), boundingBox.getYsize(), boundingBox.getZsize());
        return target.bounds(level).inflate(distance, 0.5, distance).move(target.blockPos()).intersects(movedBoundBox);
    }

    protected boolean targetIsReachableFromPosition(Level level, boolean canReachTarget, Vec3 pos, CrystalGolemTarget target, CrystalGolem body) {
        return canReachTarget && this.canSeeAnyTargetSide(target, level, body, pos);
    }

    protected boolean canSeeAnyTargetSide(CrystalGolemTarget target, Level level, CrystalGolem body, Vec3 eyePosition) {
        Vec3 center = Vec3.atCenterOf(target.blockPos());
        return Direction.stream()
                .map(direction -> center.add(0.5 * direction.getStepX(), 0.5 * direction.getStepY(), 0.5 * direction.getStepZ()))
                .map(hitTarget -> level.clip(new ClipContext(eyePosition, hitTarget, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, body)))
                .anyMatch(hitResult -> hitResult.getType() == HitResult.Type.BLOCK && hitResult.getBlockPos().equals(target.blockPos()));
    }

    protected Set<T> getVisitedPositions(CrystalGolem body) {
        return body.getBrain().getMemory(visitedMemoryModule()).orElse(Set.of());
    }

    protected Set<T> getUnreachablePositions(CrystalGolem body) {
        return body.getBrain().getMemory(unreachableMemoryModule()).orElse(Set.of());
    }

    protected void setVisitedBlockPos(CrystalGolem body, T position) {
        Set<T> visitedPositions = new HashSet<>(getVisitedPositions(body));
        visitedPositions.add(position);
        if (visitedPositions.size() > 10) {
            this.enterCooldownAfterNoMatchingTargetFound(body);
        } else {
            body.getBrain().setMemoryWithExpiry(visitedMemoryModule(), visitedPositions, 6000L);
        }
    }

    protected void enterCooldownAfterNoMatchingTargetFound(CrystalGolem body) {
        this.stopTargetingCurrentTarget(body);

        body.getBrain().setMemory(cooldownMemoryModule(), 140);
        body.getBrain().eraseMemory(visitedMemoryModule());
        body.getBrain().eraseMemory(unreachableMemoryModule());
    }

    protected AABB getTargetSearchArea(CrystalGolem mob) {
        int horizontalSearchDistance = this.getHorizontalSearchDistance(mob);
        return new AABB(mob.blockPosition()).inflate(horizontalSearchDistance, this.getVerticalSearchDistance(mob), horizontalSearchDistance);
    }

    protected int getHorizontalSearchDistance(CrystalGolem mob) {
        return mob.isPassenger() ? 1 : this.horizontalSearchDistance;
    }

    protected int getVerticalSearchDistance(CrystalGolem mob) {
        return mob.isPassenger() ? 1 : this.verticalSearchDistance;
    }

    protected Vec3 getCenterPos(CrystalGolem body) {
        return this.setMiddleYPosition(body, body.position());
    }

    protected void walkTowardsTarget(CrystalGolem body, CrystalGolemTarget target) {
        if (target != null) {
            BehaviorUtils.setWalkAndLookTargetMemories(body, target.blockPos(), this.speedModifier, 0);
        }
    }

    protected abstract MemoryModuleType<Set<T>> visitedMemoryModule();

    protected abstract MemoryModuleType<Set<T>> unreachableMemoryModule();

    protected abstract MemoryModuleType<Integer> cooldownMemoryModule();

    protected abstract void stopTargetingCurrentTarget(CrystalGolem body);
}
