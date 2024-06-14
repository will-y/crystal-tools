package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToolHealPayload() implements CustomPacketPayload {
    public static ToolHealPayload INSTANCE = new ToolHealPayload();
    public static final Type<ToolHealPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "tool_heal"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolHealPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
