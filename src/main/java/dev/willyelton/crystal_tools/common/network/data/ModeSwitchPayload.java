package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ModeSwitchPayload(boolean hasShiftDown, boolean hasCtrlDown, boolean hasAltDown) implements CustomPacketPayload {
    public static final Type<ModeSwitchPayload> TYPE = new Type<>(new ResourceLocation(CrystalTools.MODID, "mode_switch"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ModeSwitchPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ModeSwitchPayload::hasShiftDown,
            ByteBufCodecs.BOOL, ModeSwitchPayload::hasCtrlDown,
            ByteBufCodecs.BOOL, ModeSwitchPayload::hasAltDown,
            ModeSwitchPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
