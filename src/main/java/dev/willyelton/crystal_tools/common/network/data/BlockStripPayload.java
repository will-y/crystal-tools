package dev.willyelton.crystal_tools.common.network.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public record BlockStripPayload(BlockPos blockPos, InteractionHand hand, BlockState strippedState) implements CustomPacketPayload {
    public static final Type<BlockStripPayload> TYPE = new Type<>(rl("block_strip"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStripPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockStripPayload::blockPos,
            NeoForgeStreamCodecs.enumCodec(InteractionHand.class), BlockStripPayload::hand,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), BlockStripPayload::strippedState,
            BlockStripPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
