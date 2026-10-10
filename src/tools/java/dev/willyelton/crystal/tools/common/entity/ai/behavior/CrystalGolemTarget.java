package dev.willyelton.crystal.tools.common.entity.ai.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public interface CrystalGolemTarget {
    BlockPos blockPos();

    AABB bounds(Level level);
}
