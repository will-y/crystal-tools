package dev.willyelton.crystal_tools.levelable.block.entity;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class LevelableBlockEntity extends BlockEntity {
    public LevelableBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public abstract String getBlockType();

    public void addExp(Level level, BlockPos blockPos, LivingEntity player) {
        addExp(level, blockPos, player, 1);
    }

    public void addExp(Level level, BlockPos blockPos, LivingEntity livingEntity, int amount) {
        int newExperience = (int) NBTUtils.addValueToTag(this.getPersistentData(), "experience", amount);
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(this.getPersistentData(), "experience_cap", CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());

        if (newExperience >= experienceCap) {
            // level up
            NBTUtils.addValueToTag(this.getPersistentData(), "skill_points", 1);
            // play level up sound
            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 1.0F);
            if (livingEntity instanceof Player player) {
                player.displayClientMessage(Component.literal(this.getBlockType() + " Leveled Up (" + this.getSkillPoints() + " Unspent Points)"), true);
            }
            NBTUtils.setValue(this.getPersistentData(), "experience", Math.max(0, newExperience - experienceCap));
            ToolUtils.increaseExpCap(this.getPersistentData());
        }
    }

    public int getSkillPoints() {
        return (int) NBTUtils.getFloatOrAddKey(this.getPersistentData(), "skill_points");
    }
}
