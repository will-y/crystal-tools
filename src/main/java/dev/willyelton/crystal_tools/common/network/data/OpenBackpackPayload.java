package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OpenBackpackPayload(int slotId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenBackpackPayload> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CrystalTools.MODID, "open_backpack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBackpackPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, OpenBackpackPayload::slotId,
            OpenBackpackPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
