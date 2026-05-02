package dev.willyelton.crystal.core.common.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

public record PointsFromXpPayload(int points, boolean item) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PointsFromXpPayload> TYPE = new CustomPacketPayload.Type<>(baseRl("points_from_xp"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PointsFromXpPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PointsFromXpPayload::points,
            ByteBufCodecs.BOOL, PointsFromXpPayload::item,
            PointsFromXpPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
