package dev.willyelton.crystal.tools.common.levelable.tool;

import dev.willyelton.crystal.tools.common.components.DataComponents;
import net.minecraft.world.item.ItemInstance;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class HoeLevelableTool extends DiggerLevelableTool {
    public HoeLevelableTool(Properties properties) {
        super(properties.hoe(CRYSTAL, -4.0F, 0.0F));
    }

    @Override
    public boolean canPerformAction(ItemInstance itemInstance, ItemAbility itemAbility) {
        return itemInstance.getOrDefault(DataComponents.SHEAR, false) && ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(itemAbility);
    }
}
