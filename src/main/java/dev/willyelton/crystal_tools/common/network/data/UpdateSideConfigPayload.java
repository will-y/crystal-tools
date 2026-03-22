package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.common.levelable.block.entity.data.SideConfigOption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public record UpdateSideConfigPayload(BlockPos pos, Direction direction, SideConfigOption option) implements CustomPacketPayload {

    public static final Type<UpdateSideConfigPayload> TYPE = new Type<>(rl("update_side_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf,  UpdateSideConfigPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, UpdateSideConfigPayload::pos,
            Direction.STREAM_CODEC, UpdateSideConfigPayload::direction,
            SideConfigOption.STREAM_CODEC, UpdateSideConfigPayload::option,
            UpdateSideConfigPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
