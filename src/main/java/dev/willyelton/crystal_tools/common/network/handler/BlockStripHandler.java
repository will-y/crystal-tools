package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.levelable.tool.LevelableTool;
import dev.willyelton.crystal_tools.common.network.data.BlockStripPayload;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BlockStripHandler {
    public static BlockStripHandler INSTANCE = new BlockStripHandler();

    public void handle(final BlockStripPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack tool = player.getItemInHand(payload.hand());
            if (tool.getItem() instanceof LevelableTool levelableTool) {
                ToolUseUtils.stripBlock(levelableTool, player.level(), tool, player, payload.blockPos(),
                        InteractionHand.MAIN_HAND, payload.strippedState());
            }
        });
    }
}
