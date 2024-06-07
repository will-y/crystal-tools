package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record BlockStripPayload(BlockPos blockPos, InteractionHand hand, BlockState strippedState) implements CustomPacketPayload {
    // TODO: Try this ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY) (ClientboundBlockUpdatePacket)
    // Probably not that big of a deal since these packets are rare
    private static final StreamCodec<ByteBuf, BlockState> BLOCKSTATE_STREAM_CODEC = ByteBufCodecs.fromCodec(BlockState.CODEC);

    public static final Type<BlockStripPayload> TYPE = new Type<>(new ResourceLocation(CrystalTools.MODID, "block_strip"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockStripPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockStripPayload::blockPos,
            NeoForgeStreamCodecs.enumCodec(InteractionHand.class), BlockStripPayload::hand,
            BLOCKSTATE_STREAM_CODEC, BlockStripPayload::strippedState,
            BlockStripPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
