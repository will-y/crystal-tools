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

public final class AttributeNode extends SkillDataNode {
    public static final Codec<AttributeNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(AttributeNode::getId),
            Codec.STRING.fieldOf("name").forGetter(AttributeNode::getName),
            Codec.STRING.fieldOf("description").forGetter(AttributeNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(AttributeNode::getLimit),
            ResourceLocation.CODEC.listOf().fieldOf("key").forGetter(AttributeNode::getKeys),
            Codec.FLOAT.fieldOf("value").forGetter(AttributeNode::getValue),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(AttributeNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText()))
    ).apply(instance, AttributeNode::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AttributeNode::getId,
            ByteBufCodecs.STRING_UTF8, AttributeNode::getName,
            ByteBufCodecs.STRING_UTF8, AttributeNode::getDescription,
            ByteBufCodecs.VAR_INT, AttributeNode::getLimit,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), AttributeNode::getKeys,
            ByteBufCodecs.FLOAT, AttributeNode::getValue,
            ByteBufCodecs.fromCodec(SkillDataRequirement.CODEC.listOf().fieldOf("requirements").codec()), AttributeNode::getRequirements, // TODO
            ByteBufCodecs.fromCodec(SkillSubText.CODEC.optionalFieldOf("subtext").codec()), n -> Optional.ofNullable(n.getSkillSubText()), // TODO
            AttributeNode::new);
    private final float value;

    // TODO: Going to need to add slot or something here or in the skill data (probably there)
    // Do a list if possible
    public AttributeNode(int id, String name, String description, int limit, List<ResourceLocation> key, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
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

                newModifiers = newModifiers.withModifierAdded(optionalAttribute.get(),
                        new AttributeModifier(rl, bonusAmount + value * pointsToSpend, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND);
            }
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, newModifiers);
    }
}
