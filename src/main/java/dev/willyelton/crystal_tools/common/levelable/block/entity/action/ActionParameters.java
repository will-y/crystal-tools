package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ActionParameters(int maxTickCounter, float durabilityModifier, int range) {
    public ActionParameters(int maxTickCounter) {
        this(maxTickCounter, 1.0f, 0);
    }

    public static Codec<ActionParameters> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("maxTickCounter", -1).forGetter(ActionParameters::maxTickCounter),
            Codec.FLOAT.optionalFieldOf("durabilityModifier", 1.0F).forGetter(ActionParameters::durabilityModifier),
            Codec.INT.optionalFieldOf("range", 0).forGetter(ActionParameters::range)
    ).apply(instance, ActionParameters::new));
}
