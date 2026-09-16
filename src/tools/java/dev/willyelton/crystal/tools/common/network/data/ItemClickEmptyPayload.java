package dev.willyelton.crystal.tools.common.network.data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;

import static dev.willyelton.crystal.tools.CrystalTools.rl;

public record ItemClickEmptyPayload(InteractionHand hand, boolean shifting) implements CustomPacketPayload  {
    public static final Type<ItemClickEmptyPayload> TYPE = new Type<>(rl("item_click_empty"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemClickEmptyPayload> STREAM_CODEC = StreamCodec.composite(
            InteractionHand.STREAM_CODEC, ItemClickEmptyPayload::hand,
            ByteBufCodecs.BOOL, ItemClickEmptyPayload::shifting,
            ItemClickEmptyPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
