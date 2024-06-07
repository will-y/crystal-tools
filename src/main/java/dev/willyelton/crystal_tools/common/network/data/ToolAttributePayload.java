package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ToolAttributePayload(String key, float value, int id) implements CustomPacketPayload {
    public static final Type<ToolAttributePayload> TYPE = new Type<>(new ResourceLocation(CrystalTools.MODID, "block_attribute"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ToolAttributePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ToolAttributePayload::key,
            ByteBufCodecs.FLOAT, ToolAttributePayload::value,
            ByteBufCodecs.INT, ToolAttributePayload::id,
            ToolAttributePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
