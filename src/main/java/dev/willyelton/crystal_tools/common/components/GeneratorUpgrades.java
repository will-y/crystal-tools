package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.utils.CodecUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GeneratorUpgrades(int feGeneration, float fuelEfficiency, int feStorage, boolean redstoneControl,
                                boolean saveFuel, boolean metalGenerator, boolean foodGenerator, boolean gemGenerator) {
    public static final Codec<GeneratorUpgrades> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("feGeneration").forGetter(GeneratorUpgrades::feGeneration),
            Codec.FLOAT.fieldOf("fuelEfficiency").forGetter(GeneratorUpgrades::fuelEfficiency),
            Codec.INT.fieldOf("feStorage").forGetter(GeneratorUpgrades::feStorage),
            Codec.BOOL.fieldOf("redstoneControl").forGetter(GeneratorUpgrades::redstoneControl),
            Codec.BOOL.fieldOf("saveFuel").forGetter(GeneratorUpgrades::saveFuel),
            Codec.BOOL.fieldOf("metalGenerator").forGetter(GeneratorUpgrades::metalGenerator),
            Codec.BOOL.fieldOf("foodGenerator").forGetter(GeneratorUpgrades::foodGenerator),
            Codec.BOOL.fieldOf("gemGenerator").forGetter(GeneratorUpgrades::gemGenerator)
    ).apply(instance, GeneratorUpgrades::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GeneratorUpgrades> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, GeneratorUpgrades::feGeneration,
            ByteBufCodecs.FLOAT, GeneratorUpgrades::fuelEfficiency,
            ByteBufCodecs.INT, GeneratorUpgrades::feStorage,
            ByteBufCodecs.BOOL, GeneratorUpgrades::redstoneControl,
            ByteBufCodecs.BOOL, GeneratorUpgrades::saveFuel,
            ByteBufCodecs.BOOL, GeneratorUpgrades::metalGenerator,
            ByteBufCodecs.BOOL, GeneratorUpgrades::foodGenerator,
            ByteBufCodecs.BOOL, GeneratorUpgrades::gemGenerator,
            GeneratorUpgrades::new);
}
