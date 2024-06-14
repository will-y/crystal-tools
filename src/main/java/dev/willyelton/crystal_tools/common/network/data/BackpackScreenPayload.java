package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record BackpackScreenPayload(PickupType pickupType) implements CustomPacketPayload {
    public static final Type<BackpackScreenPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "backpack_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackScreenPayload> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(PickupType.class), BackpackScreenPayload::pickupType,
            BackpackScreenPayload::new);

    @Override
    public Type<? extends BackpackScreenPayload> type() {
        return TYPE;
    }

    public enum PickupType {
        PICKUP_WHITELIST, PICKUP_BLACKLIST, SORT;
    }
}
