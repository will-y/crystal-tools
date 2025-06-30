package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public record QuarryData(BlockPos miningAt, float currentProgress, BlockState miningState,
                         boolean finished, List<ItemStack> waitingStacks, int currentEnergy, boolean whitelist) {
    public static final Codec<QuarryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("miningAt").forGetter(QuarryData::miningAt),
            Codec.FLOAT.fieldOf("currentProgress").forGetter(QuarryData::currentProgress),
            BlockState.CODEC.optionalFieldOf("miningState").xmap(o -> o.orElse(Blocks.AIR.defaultBlockState()), Optional::ofNullable).forGetter(QuarryData::miningState),
            Codec.BOOL.fieldOf("finished").forGetter(QuarryData::finished),
            ItemStack.CODEC.listOf().fieldOf("waitingStacks").forGetter(QuarryData::waitingStacks),
            Codec.INT.fieldOf("currentEnergy").forGetter(QuarryData::currentEnergy),
            Codec.BOOL.fieldOf("whitelist").forGetter(QuarryData::whitelist)
    ).apply(instance, QuarryData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuarryData> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, QuarryData::miningAt,
            ByteBufCodecs.FLOAT, QuarryData::currentProgress,
            ByteBufCodecs.optional(ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)).map(blockState -> blockState.orElse(Blocks.AIR.defaultBlockState()), Optional::ofNullable), QuarryData::miningState,
            ByteBufCodecs.BOOL, QuarryData::finished,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), QuarryData::waitingStacks,
            ByteBufCodecs.INT, QuarryData::currentEnergy,
            ByteBufCodecs.BOOL, QuarryData::whitelist,
            QuarryData::new);
}
