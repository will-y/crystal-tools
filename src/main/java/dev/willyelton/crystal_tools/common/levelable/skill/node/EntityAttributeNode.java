package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirements;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Optional;

public class EntityAttributeNode extends SkillDataNode implements EntityNode {
    public static final Codec<EntityAttributeNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(EntityAttributeNode::getId),
            Codec.STRING.fieldOf("name").forGetter(EntityAttributeNode::getName),
            Codec.STRING.fieldOf("description").forGetter(EntityAttributeNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(EntityAttributeNode::getLimit),
            ResourceLocation.CODEC.listOf().fieldOf("key").forGetter(EntityAttributeNode::getKeys),
            Codec.FLOAT.fieldOf("value").forGetter(EntityAttributeNode::getValue),
            SkillDataRequirements.CODEC.listOf().fieldOf("requirements").forGetter(EntityAttributeNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText()))
    ).apply(instance, EntityAttributeNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityAttributeNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EntityAttributeNode::getId,
            ByteBufCodecs.STRING_UTF8, EntityAttributeNode::getName,
            ByteBufCodecs.STRING_UTF8, EntityAttributeNode::getDescription,
            ByteBufCodecs.VAR_INT, EntityAttributeNode::getLimit,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), EntityAttributeNode::getKeys,
            ByteBufCodecs.FLOAT, EntityAttributeNode::getValue,
            SkillDataRequirements.STREAM_CODEC.apply(ByteBufCodecs.list()), EntityAttributeNode::getRequirements,
            ByteBufCodecs.optional(SkillSubText.STREAM_CODEC), n -> Optional.ofNullable(n.getSkillSubText()),
            EntityAttributeNode::new);

    private final float value;

    public EntityAttributeNode(int id, String name, String description, int limit, List<ResourceLocation> keys, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        super(id, name, description, limit, keys, requirements, skillSubText.orElse(null));
        this.value = value;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return SkillNodeType.ENTITY_ATTRIBUTE;
    }

    @Override
    public void processNode(SkillData skillData, LivingEntity entity, int pointsToSpend, RegistryAccess registryAccess) {
        Registry<Attribute> attributeRegistry = registryAccess.lookupOrThrow(Registries.ATTRIBUTE);

        for (ResourceLocation key : getKeys()) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, key.getPath());
            Optional<Holder.Reference<Attribute>> optionalAttribute = attributeRegistry.get(key);
            if (optionalAttribute.isPresent()) {
                double existingModifier = 0;

                Holder<Attribute> attributeHolder = optionalAttribute.get();

                if (entity.getAttributes().hasModifier(attributeHolder, id)) {
                    existingModifier = entity.getAttributes().getModifierValue(attributeHolder, id);
                }

                AttributeModifier modifier = new AttributeModifier(id, existingModifier + value, AttributeModifier.Operation.ADD_VALUE);

                var attr = entity.getAttributes().getInstance(attributeHolder);

                if (attr != null) {
                    attr.removeModifier(modifier);
                    attr.addPermanentModifier(modifier);
                }
            }
        }
    }

    private float getValue() {
        return this.value;
    }
}
