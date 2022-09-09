package dev.willyelton.crystal_tools.levelable.tool;

import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class PickaxeLevelableTool extends DiggerLevelableTool {
    public PickaxeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_PICKAXE, "pickaxe");
    }

    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        return ToolUseUtils.useOnTorch(context, this);
    }
}
