package dev.willyelton.crystal.tools.common.levelable.tool;

import dev.willyelton.crystal.tools.utils.ToolUseUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;


public class ShovelLevelableTool extends DiggerLevelableTool {
    public ShovelLevelableTool(Properties properties) {
        super(properties.shovel(CRYSTAL, 1.5F, -3.0F));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return ToolUseUtils.useOnShovel3x3(context);
    }

    @Override
    public boolean canPerformAction(ItemInstance instance, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
    }
}
