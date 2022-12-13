package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class PickaxeLevelableTool extends DiggerLevelableTool {
    public PickaxeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_PICKAXE, "pickaxe", 1, -2.8F);
    }

    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
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
