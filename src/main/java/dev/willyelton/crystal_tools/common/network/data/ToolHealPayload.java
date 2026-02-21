package dev.willyelton.crystal_tools.common.network.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public record ToolHealPayload() implements CustomPacketPayload {
    public static ToolHealPayload INSTANCE = new ToolHealPayload();
    public static final Type<ToolHealPayload> TYPE = new Type<>(rl("tool_heal"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolHealPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
