package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.levelable.tool.LevelableTool;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BlockBreakPacket {
    private final BlockPos blockPos;

    public BlockBreakPacket(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockBreakPacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player == null) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        if (tool.getItem() instanceof LevelableTool levelableTool) {
            levelableTool.breakBlock(tool, player.level(), blockPos, player);
        }
    }
}
