package dev.willyelton.crystal_tools.common.network.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public record TriggerRocketPayload() implements CustomPacketPayload {
    public static final TriggerRocketPayload INSTANCE = new TriggerRocketPayload();
    public static final Type<TriggerRocketPayload> TYPE = new Type<>(rl("trigger_rocket"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerRocketPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
