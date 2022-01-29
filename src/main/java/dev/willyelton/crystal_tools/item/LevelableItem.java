package dev.willyelton.crystal_tools.item;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public interface LevelableItem {
    int AUTO_REPAIR_COUNTER = 50;
    // Moved to config
//    int BASE_EXPERIENCE_CAP = 50;
//    float EXPERIENCE_CAP_MULTIPLIER = 1.25F;

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
        int experienceCap = (int) NBTUtils.getFloatOrAddKey(tool, "experience_cap", CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());

        if (experienceCap == 0) {
            // fist time
            NBTUtils.setValue(tool, "experience_cap", CrystalToolsConfig.BASE_EXPERIENCE_CAP.get());
            experienceCap = CrystalToolsConfig.BASE_EXPERIENCE_CAP.get();
        }

        if (newExperience >= experienceCap) {
            // level up
            NBTUtils.addValueToTag(tool, "skill_points", 1);
            // copied from LivingEntity item breaking sound
            // play level up sound
            level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.8F, 1.0F);
            // TODO: Add chat message thing

            NBTUtils.setValue(tool, "experience", Math.max(0, newExperience - experienceCap));
            NBTUtils.setValue(tool, "experience_cap", (float) (experienceCap * CrystalToolsConfig.EXPERIENCE_MULTIPLIER.get()));
        }
    }

    String getItemType();

    int getMaxDamage(ItemStack itemStack);
}
