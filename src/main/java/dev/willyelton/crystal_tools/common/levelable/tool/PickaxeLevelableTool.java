package dev.willyelton.crystal_tools.common.levelable.tool;

import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class PickaxeLevelableTool extends DiggerLevelableTool {
    public PickaxeLevelableTool(Item.Properties properties) {
        super(properties.pickaxe(CRYSTAL, 1.0F, -2.8F), "pickaxe");
    }

    public InteractionResult useOn(UseOnContext context) {
        if (this.isDisabled()) {
            context.getItemInHand().shrink(1);
            return InteractionResult.FAIL;
        }
        return ToolUseUtils.useOnTorch(context, this);
    }

    @Override
    public boolean isDisabled() {
        return CrystalToolsConfig.DISABLE_PICKAXE.get();
    }
}
