package dev.willyelton.crystal_tools.common.datamap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record GeneratorFuelData(int burnTime, int bonusGeneration) {
    public static final Codec<GeneratorFuelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("burnTime").forGetter(GeneratorFuelData::burnTime),
            Codec.INT.fieldOf("bonusGeneration").forGetter(GeneratorFuelData::bonusGeneration)
    ).apply(instance, GeneratorFuelData::new));
}
