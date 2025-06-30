package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PointsFromXpPayload(int points, boolean item) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PointsFromXpPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "points_from_xp"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PointsFromXpPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PointsFromXpPayload::points,
            ByteBufCodecs.BOOL, PointsFromXpPayload::item,
            PointsFromXpPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
