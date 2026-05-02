package dev.willyelton.crystal.core.common.network.payload;

import dev.willyelton.crystal.core.common.inventory.container.SubScreenType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

public record OpenSubScreenPayload(SubScreenType subScreenType) implements CustomPacketPayload {
    public static final Type<OpenSubScreenPayload> TYPE = new Type<>(baseRl("open_sub_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenSubScreenPayload> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(SubScreenType.class), OpenSubScreenPayload::subScreenType,
            OpenSubScreenPayload::new);


    @Override
    public Type<? extends OpenSubScreenPayload> type() {
        return TYPE;
    }
}
