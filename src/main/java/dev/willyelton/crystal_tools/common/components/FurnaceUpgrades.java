package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record FurnaceUpgrades(float speed, int fuelEfficiency, int slots, int fuelSlots, boolean balance,
                              float expModifier, boolean saveFuel) {
    public static final Codec<FurnaceUpgrades> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("speed").forGetter(FurnaceUpgrades::speed),
            Codec.INT.fieldOf("fuelEfficiency").forGetter(FurnaceUpgrades::fuelEfficiency),
            Codec.INT.fieldOf("slots").forGetter(FurnaceUpgrades::slots),
            Codec.INT.fieldOf("fuelSlots").forGetter(FurnaceUpgrades::fuelSlots),
            Codec.BOOL.fieldOf("balance").forGetter(FurnaceUpgrades::balance),
            Codec.FLOAT.fieldOf("autoOutput").forGetter(FurnaceUpgrades::expModifier),
            Codec.BOOL.fieldOf("saveFuel").forGetter(FurnaceUpgrades::saveFuel)
    ).apply(instance, FurnaceUpgrades::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FurnaceUpgrades> STREAM_CODEC = NeoForgeStreamCodecs.composite(
            ByteBufCodecs.FLOAT, FurnaceUpgrades::speed,
            ByteBufCodecs.INT, FurnaceUpgrades::fuelEfficiency,
            ByteBufCodecs.INT, FurnaceUpgrades::slots,
            ByteBufCodecs.INT, FurnaceUpgrades::fuelSlots,
            ByteBufCodecs.BOOL, FurnaceUpgrades::balance,
            ByteBufCodecs.FLOAT, FurnaceUpgrades::expModifier,
            ByteBufCodecs.BOOL, FurnaceUpgrades::saveFuel,
            FurnaceUpgrades::new);
}
