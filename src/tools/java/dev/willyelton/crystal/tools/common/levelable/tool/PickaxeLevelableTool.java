package dev.willyelton.crystal.tools.common.levelable.tool;

import dev.willyelton.crystal.tools.utils.ToolUseUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public class PickaxeLevelableTool extends DiggerLevelableTool {
    public PickaxeLevelableTool(Properties properties) {
        super(properties.pickaxe(CRYSTAL, 1.0F, -2.8F));
    }

    public InteractionResult useOn(UseOnContext context) {
        return ToolUseUtils.useOnTorch(context);
    }
}
