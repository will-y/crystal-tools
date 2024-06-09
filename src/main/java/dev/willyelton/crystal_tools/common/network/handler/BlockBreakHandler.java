package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.levelable.tool.LevelableTool;
import dev.willyelton.crystal_tools.common.network.data.BlockBreakPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BlockBreakHandler {
    public static BlockBreakHandler INSTANCE = new BlockBreakHandler();

    public void handle(final BlockBreakPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            ItemStack tool = player.getMainHandItem();
            if (tool.getItem() instanceof LevelableTool levelableTool) {
                levelableTool.breakBlock(tool, player.level(), payload.blockPos(), player);
            }
        });
    }
}
