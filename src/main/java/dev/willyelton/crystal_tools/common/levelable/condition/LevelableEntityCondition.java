package dev.willyelton.crystal_tools.common.levelable.condition;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface LevelableEntityCondition extends LevelableCondition {
    boolean check(LivingEntity livingEntity, Player player);
}
