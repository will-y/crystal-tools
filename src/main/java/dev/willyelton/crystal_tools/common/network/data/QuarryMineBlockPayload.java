package dev.willyelton.crystal_tools.common.network.data;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record QuarryMineBlockPayload(BlockPos quarryPos, BlockPos miningPos, BlockState state) implements CustomPacketPayload {
    public static final Type<QuarryMineBlockPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CrystalTools.MODID, "quarry_mine_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuarryMineBlockPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, QuarryMineBlockPayload::quarryPos,
            BlockPos.STREAM_CODEC, QuarryMineBlockPayload::miningPos,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), QuarryMineBlockPayload::state,
            QuarryMineBlockPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
