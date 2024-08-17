package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;
import java.util.List;

public record LevelableBlockEntityData(int skillPoints, List<Integer> points, int exp, int expCap) {
    public static final Codec<LevelableBlockEntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("skillPoints").forGetter(LevelableBlockEntityData::skillPoints),
            Codec.INT.listOf().fieldOf("points").forGetter(LevelableBlockEntityData::points),
            Codec.INT.fieldOf("exp").forGetter(LevelableBlockEntityData::exp),
            Codec.INT.fieldOf("expCap").forGetter(LevelableBlockEntityData::expCap)
    ).apply(instance, LevelableBlockEntityData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LevelableBlockEntityData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, LevelableBlockEntityData::skillPoints,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), LevelableBlockEntityData::points,
            ByteBufCodecs.INT, LevelableBlockEntityData::exp,
            ByteBufCodecs.INT, LevelableBlockEntityData::expCap,
            LevelableBlockEntityData::new);

    public LevelableBlockEntityData(int skillPoints) {
        this(skillPoints, Arrays.stream(new int[100]).boxed().toList(), 0,0);
    }
}
