package dev.willyelton.crystal.tools.common.network.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal.tools.CrystalTools.rl;

public record MagnetModePayload(boolean hasShiftDown) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MagnetModePayload> TYPE = new CustomPacketPayload.Type<>(rl("magnet_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MagnetModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, MagnetModePayload::hasShiftDown,
            MagnetModePayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
