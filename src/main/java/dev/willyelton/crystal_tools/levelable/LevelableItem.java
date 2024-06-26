package dev.willyelton.crystal_tools.levelable;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public interface LevelableItem {
   Tier INITIAL_TIER = Tiers.NETHERITE;

    default void addExp(ItemStack tool, Level level, BlockPos blockPos, LivingEntity player) {
        addExp(tool, level, blockPos, player, 1);
    }

    default void addExp(ItemStack tool, Level level, BlockPos blockPos,LivingEntity livingEntity, int amount) {
        int newExperience = (int) NBTUtils.addValueToTag(tool, "experience", amount);
        int experienceCap = getExperienceCap(tool);

        if (newExperience >= experienceCap) {
            // level up
            NBTUtils.addValueToTag(tool, "skill_points", 1);
            // play level up sound
            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 1.0F);
            if (livingEntity instanceof Player player) {
                if (tool.getItem() instanceof LevelableItem item) {
                    player.displayClientMessage(Component.literal(tool.getItem().getName(tool).getString() + " Leveled Up (" + item.getSkillPoints(tool) + " Unspent Points)"), true);
                }
            }
            NBTUtils.setValue(tool, "experience", Math.max(0, newExperience - experienceCap));
            ToolUtils.increaseExpCap(tool);
        }
    }

    default int getSkillPoints(ItemStack stack) {
        return (int) NBTUtils.getFloatOrAddKey(stack, "skill_points");
    }

    default int getExperienceCap(ItemStack tool) {
        return (int) NBTUtils.getFloatOrAddKey(tool, "experience_cap", CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());
    }

    String getItemType();

    int getMaxDamage(ItemStack itemStack);

    boolean isDisabled();
}
