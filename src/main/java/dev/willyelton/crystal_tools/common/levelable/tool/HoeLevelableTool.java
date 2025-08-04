package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class HoeLevelableTool extends DiggerLevelableTool {
    public HoeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_HOE, "hoe", -4, 0);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (this.isDisabled()) {
            context.getItemInHand().shrink(1);
            return InteractionResult.FAIL;
        }

        return ToolUseUtils.useOnHoe3x3(context, this);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_HOE_ACTIONS.contains(itemAbility) ||
                (stack.getOrDefault(DataComponents.SHEAR, false) && ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(itemAbility));
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_HOE.get();
    }
}
