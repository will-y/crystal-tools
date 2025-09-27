package dev.willyelton.crystal_tools.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GeneratorFuelData(int burnTime, int bonusGeneration) {
    public static final Codec<GeneratorFuelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("burnTime").forGetter(GeneratorFuelData::burnTime),
            Codec.INT.fieldOf("bonusGeneration").forGetter(GeneratorFuelData::bonusGeneration)
    ).apply(instance, GeneratorFuelData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GeneratorFuelData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GeneratorFuelData::burnTime,
            ByteBufCodecs.INT, GeneratorFuelData::bonusGeneration,
            GeneratorFuelData::new);
}
