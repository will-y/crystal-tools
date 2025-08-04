package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility) ||
                (stack.getOrDefault(DataComponents.SHEAR, false) && ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(itemAbility));
    }
}
