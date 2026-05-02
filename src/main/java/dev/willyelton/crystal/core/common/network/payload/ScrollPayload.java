package dev.willyelton.crystal.core.common.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

public record ScrollPayload(int row) implements CustomPacketPayload {
    public static final Type<ScrollPayload> TYPE = new Type<>(baseRl("scroll"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ScrollPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ScrollPayload::row,
            ScrollPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
