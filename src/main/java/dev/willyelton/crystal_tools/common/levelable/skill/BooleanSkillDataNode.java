package dev.willyelton.crystal_tools.common.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;

public final class BooleanSkillDataNode extends SkillDataNode {
    public static final Codec<BooleanSkillDataNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(BooleanSkillDataNode::getId),
            Codec.STRING.fieldOf("name").forGetter(BooleanSkillDataNode::getName),
            Codec.STRING.fieldOf("description").forGetter(BooleanSkillDataNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(BooleanSkillDataNode::getLimit),
            Codec.STRING.fieldOf("key").forGetter(BooleanSkillDataNode::getKey),
            Codec.BOOL.fieldOf("value").forGetter(BooleanSkillDataNode::getValue),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(BooleanSkillDataNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(BooleanSkillDataNode::getSkillSubText)
    ).apply(instance, BooleanSkillDataNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BooleanSkillDataNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BooleanSkillDataNode::getId,
            ByteBufCodecs.STRING_UTF8, BooleanSkillDataNode::getName,
            ByteBufCodecs.STRING_UTF8, BooleanSkillDataNode::getDescription,
            ByteBufCodecs.VAR_INT, BooleanSkillDataNode::getLimit,
            ByteBufCodecs.STRING_UTF8, BooleanSkillDataNode::getKey,
            ByteBufCodecs.BOOL, BooleanSkillDataNode::getValue,
            ByteBufCodecs.fromCodec(SkillDataRequirement.CODEC.listOf().fieldOf("requirements").codec()), BooleanSkillDataNode::getRequirements, // TODO
            ByteBufCodecs.fromCodec(SkillSubText.CODEC.optionalFieldOf("subtext").codec()), BooleanSkillDataNode::getSkillSubText, // TODO
            BooleanSkillDataNode::new);

    private final boolean value;

    public BooleanSkillDataNode(int id, String name, String description, int limit, String key, Boolean value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        super(id, name, description, limit, key, requirements, skillSubText);

        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return null;
    }
}
