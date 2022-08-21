package dev.willyelton.crystal_tools.item.tool;

import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class HoeLevelableTool extends LevelableTool {
    public HoeLevelableTool() {
        super(new Item.Properties(), BlockTags.MINEABLE_WITH_HOE, "hoe");
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        return ToolUseUtils.useOnHoe(context, this);
    }
}
