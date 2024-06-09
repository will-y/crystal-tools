package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record VeinMiningPayload(boolean start) implements CustomPacketPayload {
    public static final Type<VeinMiningPayload> TYPE = new Type<>(new ResourceLocation(CrystalTools.MODID, "vein_mining"));
    public static final StreamCodec<RegistryFriendlyByteBuf, VeinMiningPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, VeinMiningPayload::start,
            VeinMiningPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
