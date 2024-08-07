package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ScrollPayload(int row) implements CustomPacketPayload {
    public static final Type<ScrollPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "scroll"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ScrollPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ScrollPayload::row,
            ScrollPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
