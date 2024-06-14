package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.List;

public record FurnaceData(int litTime, int litDuration, List<Integer> cookingProgress, List<Integer> cookingTime, float expHeld) {
    public static final Codec<FurnaceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("litTime").forGetter(FurnaceData::litTime),
            Codec.INT.fieldOf("litDuration").forGetter(FurnaceData::litDuration),
            Codec.INT.listOf().fieldOf("cookingProgress").forGetter(FurnaceData::cookingProgress),
            Codec.INT.listOf().fieldOf("cookingTime").forGetter(FurnaceData::cookingTime),
            Codec.FLOAT.fieldOf("expHeld").forGetter(FurnaceData::expHeld)
    ).apply(instance, FurnaceData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FurnaceData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, FurnaceData::litTime,
            ByteBufCodecs.INT, FurnaceData::litDuration,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), FurnaceData::cookingProgress,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), FurnaceData::cookingTime,
            ByteBufCodecs.FLOAT, FurnaceData::expHeld,
            FurnaceData::new);

    public FurnaceData() {
        this(0, 0, Collections.emptyList(), Collections.emptyList(), 0F);
    }
}
