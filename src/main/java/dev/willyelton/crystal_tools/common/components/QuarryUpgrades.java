package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record QuarryUpgrades(float speedUpgrade, boolean redstoneControl, int fortuneLevel,
                             boolean silkTouch, int extraEnergyCost, int filterRows) {
    public static final Codec<QuarryUpgrades> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("speedUpgrade").forGetter(QuarryUpgrades::speedUpgrade),
            Codec.BOOL.fieldOf("redstoneControl").forGetter(QuarryUpgrades::redstoneControl),
            Codec.INT.fieldOf("fortuneLevel").forGetter(QuarryUpgrades::fortuneLevel),
            Codec.BOOL.fieldOf("silkTouch").forGetter(QuarryUpgrades::silkTouch),
            Codec.INT.fieldOf("extraEnergyCost").forGetter(QuarryUpgrades::extraEnergyCost),
            Codec.INT.fieldOf("filterRows").forGetter(QuarryUpgrades::extraEnergyCost)
    ).apply(instance, QuarryUpgrades::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuarryUpgrades> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, QuarryUpgrades::speedUpgrade,
            ByteBufCodecs.BOOL, QuarryUpgrades::redstoneControl,
            ByteBufCodecs.INT, QuarryUpgrades::fortuneLevel,
            ByteBufCodecs.BOOL, QuarryUpgrades::silkTouch,
            ByteBufCodecs.INT, QuarryUpgrades::extraEnergyCost,
            ByteBufCodecs.INT, QuarryUpgrades::filterRows,
            QuarryUpgrades::new);
}
