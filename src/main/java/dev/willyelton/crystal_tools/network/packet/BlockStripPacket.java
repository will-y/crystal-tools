package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.levelable.tool.LevelableTool;
import dev.willyelton.crystal_tools.utils.ToolUseUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BlockStripPacket {
    private final BlockPos blockPos;
    private final InteractionHand hand;
    private final BlockState strippedState;

    public BlockStripPacket(BlockPos blockPos, InteractionHand hand, BlockState strippedState) {
        this.blockPos = blockPos;
        this.hand = hand;
        this.strippedState = strippedState;
    }

    public BlockStripPacket(FriendlyByteBuf buffer) {
        this.blockPos = buffer.readBlockPos();
        this.hand = buffer.readEnum(InteractionHand.class);
        this.strippedState = buffer.readJsonWithCodec(BlockState.CODEC);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeEnum(hand);
        buffer.writeJsonWithCodec(BlockState.CODEC, strippedState);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) {
            return;
        }

        ItemStack tool = player.getItemInHand(hand);
        if (tool.getItem() instanceof LevelableTool levelableTool) {
            ToolUseUtils.stripBlock(levelableTool, player.level(), tool, player, blockPos,
                    InteractionHand.MAIN_HAND, strippedState);
        }
    }
}
