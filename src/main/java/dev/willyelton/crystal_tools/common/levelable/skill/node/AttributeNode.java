package dev.willyelton.crystal_tools.common.levelable.skill.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillSubText;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillDataRequirement;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
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
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AttributeNode extends SkillDataNode implements ItemStackNode {
    public static final Codec<AttributeNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(AttributeNode::getId),
            Codec.STRING.fieldOf("name").forGetter(AttributeNode::getName),
            Codec.STRING.fieldOf("description").forGetter(AttributeNode::getDescription),
            Codec.INT.fieldOf("limit").forGetter(AttributeNode::getLimit),
            ResourceLocation.CODEC.listOf().fieldOf("key").forGetter(AttributeNode::getKeys),
            Codec.FLOAT.fieldOf("value").forGetter(AttributeNode::getValue),
            SkillDataRequirement.CODEC.listOf().fieldOf("requirements").forGetter(AttributeNode::getRequirements),
            SkillSubText.CODEC.optionalFieldOf("subtext").forGetter(n -> Optional.ofNullable(n.getSkillSubText())),
            Codec.BOOL.optionalFieldOf("threshold", false).forGetter(AttributeNode::isThreshold)
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

    public static final Map<Attribute, DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>>> TOGGLEABLE_ATTRIBUTES = Map.of(
            NeoForgeMod.CREATIVE_FLIGHT.value(),
            dev.willyelton.crystal_tools.common.components.DataComponents.CREATIVE_FLIGHT);

    private final float value;
    private final boolean threshold;

    public AttributeNode(int id, String name, String description, int limit, List<ResourceLocation> keys, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText, boolean threshold) {
        super(id, name, description, limit, keys, requirements, skillSubText.orElse(null));
        this.value = value;
        this.threshold = threshold;

        if (threshold) {
            this.setSubtext(new SkillSubText(String.format("Will activate after you put %s points in this node", this.getLimit()), "#ABABAB"));
        }
    }

    public AttributeNode(int id, String name, String description, int limit, List<ResourceLocation> keys, float value, List<SkillDataRequirement> requirements, Optional<SkillSubText> skillSubText) {
        this(id, name, description, limit, keys, value, requirements, skillSubText, false);
    }

    public float getValue() {
        return value;
    }

    public boolean isThreshold() {
        return threshold;
    }

    @Override
    public SkillNodeType getSkillNodeType() {
        return SkillNodeType.ATTRIBUTE;
    }

    @Override
    public void processNode(SkillData skillData, ItemStack stack, int pointsToSpend, RegistryAccess registryAccess) {
        if (threshold) {
            int totalPoints = stack.getOrDefault(dev.willyelton.crystal_tools.common.components.DataComponents.SKILL_POINT_DATA, new SkillPoints()).getPoints(this.getId());

            if (totalPoints < this.getLimit()) {
                return;
            }
        }

        if (skillData.getEquipmentSlot() == null) return;

        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        Registry<Attribute> attributeRegistry = registryAccess.lookupOrThrow(Registries.ATTRIBUTE);
        ItemAttributeModifiers newModifiers = modifiers;

        for (ResourceLocation key : getKeys()) {
            ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, key.getPath());
            Optional<Holder.Reference<Attribute>> optionalAttribute = attributeRegistry.get(key);
            double bonusAmount = 0;
            if (optionalAttribute.isPresent()) {
                Holder<Attribute> attributeHolder = optionalAttribute.get();

                if (TOGGLEABLE_ATTRIBUTES.containsKey(attributeHolder.value())) {
                    stack.set(TOGGLEABLE_ATTRIBUTES.get(attributeHolder.value()), true);
                }

                for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                    if (entry.modifier().id().equals(rl)) {
                        bonusAmount = entry.modifier().amount();
                        break;
                    }
                }

                newModifiers = newModifiers.withModifierAdded(attributeHolder,
                        new AttributeModifier(rl, bonusAmount + value * pointsToSpend, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.bySlot(skillData.getEquipmentSlot()));
            }
        }

        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, newModifiers);
    }
}
