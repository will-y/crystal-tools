package dev.willyelton.crystal_tools.common.levelable.tool;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class CrystalShield extends ShieldItem {
    public CrystalShield(Properties properties) {
        super(properties.durability(1000).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
    }
}
