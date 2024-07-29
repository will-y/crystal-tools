package dev.willyelton.crystal_tools.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RayTraceUtils {

    /**
     * Gets the block that the entity is looking at
     * @return block hit result
     */
    public static BlockHitResult rayTrace(LivingEntity entity) {
        // Modified from: https://github.com/Direwolf20-MC/MiningGadgets/blob/mc/1.20.1/src/main/java/com/direwolf20/mininggadgets/common/util/VectorHelper.java
        AttributeInstance attributeInstance = entity.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        double reach = attributeInstance == null ? 5 : attributeInstance.getValue();

        Vec3 look = entity.getLookAngle();
        Vec3 start = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        Vec3 end = new Vec3(entity.getX() + look.x * reach, entity.getY() + entity.getEyeHeight() + look.y * reach, entity.getZ() + look.z * reach);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity);

        return entity.level().clip(context);
    }
}
