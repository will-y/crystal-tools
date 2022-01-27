package dev.willyelton.crystal_tools.item;

import dev.willyelton.crystal_tools.CreativeTabs;
import dev.willyelton.crystal_tools.item.skill.SkillData;
import dev.willyelton.crystal_tools.item.skill.SkillDataNode;
import dev.willyelton.crystal_tools.item.skill.SkillNodeType;
import dev.willyelton.crystal_tools.utils.LevelUtilities;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public interface LevelableItem {
    int AUTO_REPAIR_COUNTER = 50;
    int BASE_EXPERIENCE_CAP = 50;
    float EXPERIENCE_CAP_MULTIPLIER = 1.25F;

    // Just used for default values, just at netherite for now
    Tier tier = Tiers.NETHERITE;

//    protected final String itemType;

//    public LevelableItem(Properties properties, String itemType) {
//        super(properties.defaultDurability(tier.getUses()).fireResistant().tab(CreativeTabs.CRYSTAL_TOOLS_TAB));
//        this.itemType = itemType;
//    }

//    // From TierdItem.java

    default void addExp(ItemStack tool, Level level, BlockPos blockPos) {
        addExp(tool, level, blockPos, 1);
    }

    default void addExp(ItemStack tool, Level level, BlockPos blockPos, int amount) {
        int newExperience = (int) NBTUtils.addValueToTag(tool, "experience", amount);
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(tool, "experience_cap", BASE_EXPERIENCE_CAP);

        if (experienceCap == 0) {
            // fist time
            NBTUtils.setValue(tool, "experience_cap", BASE_EXPERIENCE_CAP);
            experienceCap = BASE_EXPERIENCE_CAP;
        }

        if (newExperience >= experienceCap) {
            // level up
            NBTUtils.addValueToTag(tool, "skill_points", 1);
            // copied from LivingEntity item breaking sound
            // play level up sound
            level.playLocalSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 0.8F + level.random.nextFloat() * 0.4F, false);
            // TODO: Add chat message thing

            NBTUtils.setValue(tool, "experience", Math.max(0, newExperience - experienceCap));
            NBTUtils.setValue(tool, "experience_cap", experienceCap * EXPERIENCE_CAP_MULTIPLIER);
        }
    }

    String getItemType();

    int getMaxDamage(ItemStack itemStack);
}
