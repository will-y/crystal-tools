package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenBackpackPayload() implements CustomPacketPayload {
    public static OpenBackpackPayload INSTANCE = new OpenBackpackPayload();
    public static final CustomPacketPayload.Type<OpenBackpackPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "open_backpack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBackpackPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
