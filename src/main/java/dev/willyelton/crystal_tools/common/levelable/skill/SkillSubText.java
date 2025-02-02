package dev.willyelton.crystal_tools.common.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SkillSubText(String text, String color) {
    public static final Codec<SkillSubText> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("text").forGetter(SkillSubText::text),
            Codec.STRING.fieldOf("color").forGetter(SkillSubText::color)
    ).apply(instance, SkillSubText::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillSubText> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SkillSubText::text,
            ByteBufCodecs.STRING_UTF8, SkillSubText::color,
            SkillSubText::new);
}
