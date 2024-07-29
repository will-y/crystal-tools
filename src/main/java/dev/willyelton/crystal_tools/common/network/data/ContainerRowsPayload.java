package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ContainerRowsPayload(int rows) implements CustomPacketPayload {
    public static final Type<ContainerRowsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "container_rows"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ContainerRowsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ContainerRowsPayload::rows,
            ContainerRowsPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
