package dev.willyelton.crystal.core.common.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

public record ModeSwitchPayload(boolean hasShiftDown, boolean hasCtrlDown, boolean hasAltDown) implements CustomPacketPayload {
    public static final Type<ModeSwitchPayload> TYPE = new Type<>(baseRl("mode_switch"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ModeSwitchPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ModeSwitchPayload::hasShiftDown,
            ByteBufCodecs.BOOL, ModeSwitchPayload::hasCtrlDown,
            ByteBufCodecs.BOOL, ModeSwitchPayload::hasAltDown,
            ModeSwitchPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
