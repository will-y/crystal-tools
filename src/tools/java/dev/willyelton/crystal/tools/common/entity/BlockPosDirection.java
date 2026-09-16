package dev.willyelton.crystal.tools.common.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record BlockPosDirection(BlockPos pos, Direction direction) {
    public static final Codec<BlockPosDirection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(BlockPosDirection::pos),
            Direction.CODEC.fieldOf("direction").forGetter(BlockPosDirection::direction)
    ).apply(instance, BlockPosDirection::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPosDirection> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockPosDirection::pos,
            Direction.STREAM_CODEC, BlockPosDirection::direction,
            BlockPosDirection::new);

    @Override
    public @NotNull String toString() {
        return String.format("(%d, %d, %d) - %s", pos.getX(), pos.getY(), pos.getZ(), direction.toString());
    }
}
