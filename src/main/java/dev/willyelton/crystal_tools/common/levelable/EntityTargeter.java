package dev.willyelton.crystal_tools.common.levelable;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Can be applied to items to allow them to target entities and store
 * the entity in a datacomponent
 */
public interface EntityTargeter {
    default void refreshTarget(ItemStack stack, Level level, LivingEntity livingEntity) {
        int targetId = stack.getOrDefault(DataComponents.ENTITY_TARGET, -1);
        LivingEntity newTarget = findNewTarget(livingEntity);
        if (targetId == -1) {
            setTarget(stack, newTarget);
        } else {
            if (newTarget == null || newTarget.isRemoved()) {
                // No longer looking at anything
                // TODO: some timer here maybe, or just keep it like this
//                clearTarget(stack, level);
            } else {
                // Looking at something new
                clearTarget(stack, level);
                setTarget(stack, newTarget);
            }
        }
    }

    default @Nullable LivingEntity findNewTarget(LivingEntity user) {
        Vec3 vec3 = user.getEyePosition();
        Vec3 vec31 = user.getViewVector(1.0F);
        Vec3 vec32 = vec3.add(vec31.x * 100.0, vec31.y * 100.0, vec31.z * 100.0);
        EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(
                user.level(), user, vec3, vec32, new AABB(vec3, vec32).inflate(1.0), e -> !e.isSpectator(), 0.0F);

        if (entityhitresult != null && entityhitresult.getEntity() instanceof LivingEntity target && user.hasLineOfSight(target)) {
            return target;
        }

        return null;
    }

    default void setTarget(ItemStack stack, LivingEntity target) {
        if (target != null && !target.isRemoved()) {
            stack.set(DataComponents.ENTITY_TARGET, target.getId());
            target.setGlowingTag(true);
        }
    }

    default void clearTarget(ItemStack stack, Level level) {
        int target = stack.getOrDefault(DataComponents.ENTITY_TARGET, -1);

        if (target != -1) {
            Entity levelEntity = level.getEntity(target);
            if (levelEntity != null && !levelEntity.isRemoved()) {
                levelEntity.setGlowingTag(false);
                stack.remove(DataComponents.ENTITY_TARGET);
            }
        }
    }
}
