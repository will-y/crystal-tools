package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;


public class ShovelLevelableTool extends DiggerLevelableTool {
    public ShovelLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_SHOVEL, "shovel", 1.5F, -3.0F);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        if (this.isDisabled()) {
            pContext.getItemInHand().shrink(1);
            return InteractionResult.FAIL;
        }
        return ToolUseUtils.useOnShovel3x3(pContext, this);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_SHOVEL.get();
    }
}
