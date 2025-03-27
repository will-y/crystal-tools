package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public final class AttributeSkillDataNode extends SkillDataNode {
    public static final Codec<AttributeSkillDataNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(AttributeSkillDataNode::getId),
            Codec.STRING.fieldOf("name").forGetter(AttributeSkillDataNode::getName),
            Codec.STRING.fieldOf("description").forGetter(AttributeSkillDataNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(AttributeSkillDataNode::getLimit),
            ResourceLocation.CODEC.listOf().fieldOf("key").forGetter(AttributeSkillDataNode::getKeys),
            Codec.FLOAT.fieldOf("value").forGetter(AttributeSkillDataNode::getValue),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(AttributeSkillDataNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText()))
    ).apply(instance, AttributeSkillDataNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeSkillDataNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AttributeSkillDataNode::getId,
            ByteBufCodecs.STRING_UTF8, AttributeSkillDataNode::getName,
            ByteBufCodecs.STRING_UTF8, AttributeSkillDataNode::getDescription,
            ByteBufCodecs.VAR_INT, AttributeSkillDataNode::getLimit,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), AttributeSkillDataNode::getKeys,
            ByteBufCodecs.FLOAT, AttributeSkillDataNode::getValue,
            ByteBufCodecs.fromCodec(SkillDataRequirement.CODEC.listOf().fieldOf("requirements").codec()), AttributeSkillDataNode::getRequirements, // TODO
            ByteBufCodecs.fromCodec(SkillSubText.CODEC.optionalFieldOf("subtext").codec()), n -> Optional.ofNullable(n.getSkillSubText()), // TODO
            AttributeSkillDataNode::new);
    private final float value;

    // TODO: Any reason not to use resource location here and store a list of resource locations instead of key?
    public AttributeSkillDataNode(int id, String name, String description, int limit, List<ResourceLocation> key, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        super(id, name, description, limit, key, requirements, skillSubText.orElse(null));
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return SkillNodeType.ATTRIBUTE;
    }
}
