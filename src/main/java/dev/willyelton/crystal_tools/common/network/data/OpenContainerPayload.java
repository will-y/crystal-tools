package dev.willyelton.crystal_tools.common.network.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public record OpenContainerPayload(long packedBlockPos) implements CustomPacketPayload  {
    public static final CustomPacketPayload.Type<OpenContainerPayload> TYPE = new CustomPacketPayload.Type<>(rl("open_container"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenContainerPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, OpenContainerPayload::packedBlockPos,
            OpenContainerPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
