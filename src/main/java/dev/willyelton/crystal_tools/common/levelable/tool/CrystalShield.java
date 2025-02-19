package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class CrystalShield extends ShieldItem implements LevelableItem {

    public CrystalShield() {
        super(new Item.Properties().durability(1000).component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY));
    }

    @Override
    public String getItemType() {
        return "shield";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return super.getMaxDamage(stack);
    }
}
