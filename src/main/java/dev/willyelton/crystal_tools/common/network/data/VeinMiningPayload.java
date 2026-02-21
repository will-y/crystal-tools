package dev.willyelton.crystal_tools.common.network.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public record VeinMiningPayload(boolean start) implements CustomPacketPayload {
    public static final Type<VeinMiningPayload> TYPE = new Type<>(rl("vein_mining"));
    public static final StreamCodec<RegistryFriendlyByteBuf, VeinMiningPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, VeinMiningPayload::start,
            VeinMiningPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
