package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class HoeLevelableTool extends DiggerLevelableTool {
    public HoeLevelableTool(Item.Properties properties) {
        super(properties.hoe(CRYSTAL, -4.0F, 0.0F));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return ToolUseUtils.useOnHoe3x3(context);
    }

    @Override
    public boolean canPerformAction(ItemInstance itemInstance, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility) ||
                (itemInstance.getOrDefault(DataComponents.SHEAR, false) && ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(itemAbility));
    }
}
