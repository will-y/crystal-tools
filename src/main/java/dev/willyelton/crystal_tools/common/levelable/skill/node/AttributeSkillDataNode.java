package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

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

    // TODO: Going to need to add slot or something here or in the skill data (probably there)
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

    @Override
    public void processNode(ItemStack stack, int pointsToSpend, RegistryAccess registryAccess) {
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        Registry<Attribute> attributeRegistry = registryAccess.lookupOrThrow(Registries.ATTRIBUTE);
        ItemAttributeModifiers newModifiers = modifiers;

        for (ResourceLocation key : getKeys()) {
            ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, key.getPath());
            Optional<Holder.Reference<Attribute>> optionalAttribute = attributeRegistry.get(key);
            double bonusAmount = 0;
            if (optionalAttribute.isPresent()) {
                for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                    if (entry.attribute().is(key)) {
                        bonusAmount = entry.modifier().amount();
                        break;
                    }
                }

                newModifiers = modifiers.withModifierAdded(optionalAttribute.get(),
                        new AttributeModifier(rl, bonusAmount + value * pointsToSpend, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND);
            }
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, newModifiers);
    }
}
