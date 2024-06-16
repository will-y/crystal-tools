package dev.willyelton.crystal_tools.common.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record EffectData(String resourceLocation, int duration) {
    public static Codec<EffectData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("resourceLocation").forGetter(EffectData::resourceLocation),
            Codec.INT.fieldOf("duration").forGetter(EffectData::duration)
    ).apply(instance, EffectData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, EffectData::resourceLocation,
            ByteBufCodecs.INT, EffectData::duration,
            EffectData::new);
}
