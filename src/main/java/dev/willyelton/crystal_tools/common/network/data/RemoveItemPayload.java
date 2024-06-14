package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record RemoveItemPayload(ItemStack stack) implements CustomPacketPayload {
    public static final Type<RemoveItemPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "remove_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveItemPayload> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, RemoveItemPayload::stack,
            RemoveItemPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
