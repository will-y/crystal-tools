package dev.willyelton.crystal_tools.api.common.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal_tools.api.utils.constants.ApiConstants.baseRl;

public record ContainerRowsPayload(int rows) implements CustomPacketPayload {
    public static final Type<ContainerRowsPayload> TYPE = new Type<>(baseRl("container_rows"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ContainerRowsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ContainerRowsPayload::rows,
            ContainerRowsPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
