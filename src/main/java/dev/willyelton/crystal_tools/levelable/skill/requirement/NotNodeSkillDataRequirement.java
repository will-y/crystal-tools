package dev.willyelton.crystal_tools.levelable.skill.requirement;


import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.utils.CodecUtils;

import java.util.List;

public class NotNodeSkillDataRequirement extends NodeSkillDataRequirement {
    public NotNodeSkillDataRequirement(List<Integer> requiredNodes, List<Integer> unless) {
        super(requiredNodes, true, unless);
    }

    @Override
    public JsonElement toJson() {
        return CodecUtils.encodeOrThrow(CODEC, this);
    }

    public static final Codec<NotNodeSkillDataRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.listOf().fieldOf("not_node").forGetter(NotNodeSkillDataRequirement::getRequiredNodes),
            Codec.INT.listOf().optionalFieldOf("unless", List.of()).forGetter(NotNodeSkillDataRequirement::getUnless)
    ).apply(instance, NotNodeSkillDataRequirement::new));
}
