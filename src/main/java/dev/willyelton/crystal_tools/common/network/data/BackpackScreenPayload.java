package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record BackpackScreenPayload(BackpackAction pickupType, boolean hasShiftDown) implements CustomPacketPayload {
    public BackpackScreenPayload(BackpackAction pickupType) {
        this(pickupType, false);
    }

    public static final Type<BackpackScreenPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "backpack_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackScreenPayload> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(BackpackAction.class), BackpackScreenPayload::pickupType,
            ByteBufCodecs.BOOL, BackpackScreenPayload::hasShiftDown,
            BackpackScreenPayload::new);

    @Override
    public Type<? extends BackpackScreenPayload> type() {
        return TYPE;
    }

    public enum BackpackAction {
        PICKUP_WHITELIST, PICKUP_BLACKLIST, SORT, COMPRESS, OPEN_COMPRESSION, OPEN_FILTER, MATCH_CONTENTS, CLEAR_FILTERS, CLOSE_SUB_SCREEN, REOPEN_BACKPACK
    }
}
