package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record BlockBreakPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final Type<BlockBreakPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CrystalTools.MODID, "block_break"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockBreakPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockBreakPayload::blockPos,
            BlockBreakPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
