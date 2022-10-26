package dev.willyelton.crystal_tools.item;

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

    // Just used for default values, just at netherite for now
    Tier tier = Tiers.NETHERITE;

    default void addExp(ItemStack tool, Level level, BlockPos blockPos, LivingEntity player) {
        addExp(tool, level, blockPos, player, 1);
    }

    default void addExp(ItemStack tool, Level level, BlockPos blockPos,LivingEntity livingEntity, int amount) {
        int newExperience = (int) NBTUtils.addValueToTag(tool, "experience", amount);
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(tool, "experience_cap", CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());

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
//            NBTUtils.setValue(tool, "experience_cap", (float) (experienceCap * CrystalToolsConfig.EXPERIENCE_MULTIPLIER.get()));
            ToolUtils.increaseExpCap(tool);
        }
    }

    default int getSkillPoints(ItemStack stack) {
        return (int) NBTUtils.getFloatOrAddKey(stack, "skill_points");
    }

    String getItemType();

    int getMaxDamage(ItemStack itemStack);
}
