package dev.willyelton.crystal.tools.common.network.data;

import dev.willyelton.crystal.core.common.network.model.BackpackAction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

// TODO: This should be multiple packets
public record BackpackScreenPayload(BackpackAction pickupType, boolean hasShiftDown) implements CustomPacketPayload {
    public BackpackScreenPayload(BackpackAction pickupType) {
        this(pickupType, false);
    }

    public static final Type<BackpackScreenPayload> TYPE = new Type<>(baseRl("backpack_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackScreenPayload> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(BackpackAction.class), BackpackScreenPayload::pickupType,
            ByteBufCodecs.BOOL, BackpackScreenPayload::hasShiftDown,
            BackpackScreenPayload::new);

    @Override
    public Type<? extends BackpackScreenPayload> type() {
        return TYPE;
    }
}
