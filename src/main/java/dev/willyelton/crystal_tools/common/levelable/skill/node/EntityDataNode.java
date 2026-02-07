package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.attachment.EntitySkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirements;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public class EntityDataNode extends SkillDataNode implements EntityNode {

    public static final Codec<EntityDataNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(EntityDataNode::getId),
            Codec.STRING.fieldOf("name").forGetter(EntityDataNode::getName),
            Codec.STRING.fieldOf("description").forGetter(EntityDataNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(EntityDataNode::getLimit),
            ResourceLocation.CODEC.listOf().fieldOf("key").forGetter(EntityDataNode::getKeys),
            Codec.FLOAT.fieldOf("value").forGetter(EntityDataNode::getValue),
            SkillDataRequirements.CODEC.listOf().fieldOf("requirements").forGetter(EntityDataNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText()))
    ).apply(instance, EntityDataNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityDataNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EntityDataNode::getId,
            ByteBufCodecs.STRING_UTF8, EntityDataNode::getName,
            ByteBufCodecs.STRING_UTF8, EntityDataNode::getDescription,
            ByteBufCodecs.VAR_INT, EntityDataNode::getLimit,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), EntityDataNode::getKeys,
            ByteBufCodecs.FLOAT, EntityDataNode::getValue,
            SkillDataRequirements.STREAM_CODEC.apply(ByteBufCodecs.list()), EntityDataNode::getRequirements,
            ByteBufCodecs.optional(SkillSubText.STREAM_CODEC), n -> Optional.ofNullable(n.getSkillSubText()),
            EntityDataNode::new);

    private final float value;

    public EntityDataNode(int id, String name, String description, int limit, List<ResourceLocation> keys, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        super(id, name, description, limit, keys, requirements, skillSubText.orElse(null));

        this.value = value;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return SkillNodeType.ENTITY_DATA;
    }

    @Override
    public void processNode(SkillData skillData, LivingEntity entity, int pointsToSpend, RegistryAccess registryAccess) {
        EntitySkillData entitySkillData = entity.getData(ModRegistration.ENTITY_SKILL);

        for (ResourceLocation key : getKeys()) {
            entitySkillData.addSkill(key, pointsToSpend * this.value);
        }

        entity.syncData(ModRegistration.ENTITY_SKILL);
    }

    private float getValue() {
        return this.value;
    }
}
