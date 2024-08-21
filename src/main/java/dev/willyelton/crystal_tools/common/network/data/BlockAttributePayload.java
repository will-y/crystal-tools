package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BlockAttributePayload(String key, float value, int id, int pointsToSpend) implements CustomPacketPayload {
    public static final Type<BlockAttributePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "block_attribute"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockAttributePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BlockAttributePayload::key,
            ByteBufCodecs.FLOAT, BlockAttributePayload::value,
            ByteBufCodecs.INT, BlockAttributePayload::id,
            ByteBufCodecs.INT, BlockAttributePayload::pointsToSpend,
            BlockAttributePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
