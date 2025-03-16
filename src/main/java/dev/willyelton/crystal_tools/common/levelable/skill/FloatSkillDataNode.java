package dev.willyelton.crystal_tools.common.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;

public final class FloatSkillDataNode extends SkillDataNode {
    public static final Codec<FloatSkillDataNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(FloatSkillDataNode::getId),
            Codec.STRING.fieldOf("name").forGetter(FloatSkillDataNode::getName),
            Codec.STRING.fieldOf("description").forGetter(FloatSkillDataNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(FloatSkillDataNode::getLimit),
            Codec.STRING.fieldOf("key").forGetter(FloatSkillDataNode::getKey),
            Codec.FLOAT.fieldOf("value").forGetter(FloatSkillDataNode::getValue),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(FloatSkillDataNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(FloatSkillDataNode::getSkillSubText)
    ).apply(instance, FloatSkillDataNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FloatSkillDataNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FloatSkillDataNode::getId,
            ByteBufCodecs.STRING_UTF8, FloatSkillDataNode::getName,
            ByteBufCodecs.STRING_UTF8, FloatSkillDataNode::getDescription,
            ByteBufCodecs.VAR_INT, FloatSkillDataNode::getLimit,
            ByteBufCodecs.STRING_UTF8, FloatSkillDataNode::getKey,
            ByteBufCodecs.FLOAT, FloatSkillDataNode::getValue,
            ByteBufCodecs.fromCodec(SkillDataRequirement.CODEC.listOf().fieldOf("requirements").codec()), FloatSkillDataNode::getRequirements, // TODO
            ByteBufCodecs.fromCodec(SkillSubText.CODEC.optionalFieldOf("subtext").codec()), FloatSkillDataNode::getSkillSubText, // TODO
            FloatSkillDataNode::new);

    private final float value;

    public FloatSkillDataNode(int id, String name, String description, int limit, String key, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        super(id, name, description, limit, key, requirements, skillSubText);
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public Codec<? extends SkillDataNode> codec() {
        return null;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return null;
    }
}
