package dev.willyelton.crystal.tools.common.levelable.tool;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;


public class ShovelLevelableTool extends DiggerLevelableTool {
    public ShovelLevelableTool(Properties properties) {
        super(properties.shovel(CRYSTAL, 1.5F, -3.0F));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // TODO
        return InteractionResult.PASS;
//        return ToolUseUtils.useOnShovel3x3(context);
    }
}
