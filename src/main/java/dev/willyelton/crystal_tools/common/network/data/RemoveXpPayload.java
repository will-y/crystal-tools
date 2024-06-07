package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RemoveXpPayload(int xp) implements CustomPacketPayload {
    public static final Type<RemoveXpPayload> TYPE = new Type<>(new ResourceLocation(CrystalTools.MODID, "remove_xp"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveXpPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RemoveXpPayload::xp,
            RemoveXpPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
