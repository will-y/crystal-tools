package dev.willyelton.crystal_tools.common.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.node.DataComponentNode;
import dev.willyelton.crystal_tools.common.levelable.skill.node.EffectNode;
import dev.willyelton.crystal_tools.common.levelable.skill.node.EnchantmentNode;
import dev.willyelton.crystal_tools.common.levelable.skill.node.AttributeNode;
import dev.willyelton.crystal_tools.common.levelable.skill.node.FoodDataComponentNode;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.NodeOrSkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.NodeSkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.NotNodeSkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.SkillItemRequirement;
import dev.willyelton.crystal_tools.utils.ListUtils;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeDescriptions;
import dev.willyelton.crystal_tools.utils.constants.SkillTreeTitles;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SkillData {
    private final EquipmentSlot equipmentSlot;
    private final List<List<SkillDataNode>> nodes;
    // Flattened nodes, calculated lazily
    private List<SkillDataNode> flatNodes = null;
    // Map of nodes calculated lazily
    private Map<Integer, SkillDataNode> nodeMap = null;

    private SkillData(List<List<SkillDataNode>> nodes, EquipmentSlot equipmentSlot) {
        this.nodes = nodes;
        this.equipmentSlot = equipmentSlot;
    }

    public List<List<SkillDataNode>> getAllNodesByTier() {
        return this.nodes;
    }

    public EquipmentSlot getEquipmentSlot() {
        return this.equipmentSlot;
    }

    public List<SkillDataNode> getAllNodes() {
        if (flatNodes == null) {
            flatNodes = ListUtils.flattenList(nodes);
        }

        return flatNodes;
    }

    public Map<Integer, SkillDataNode> getNodeMap() {
        if (nodeMap == null) {
            nodeMap = nodes.stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toMap(SkillDataNode::getId, Function.identity()));
        }

        return nodeMap;
    }

    public static final Codec<SkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillDataNode.CODEC.listOf().listOf()
                    .fieldOf("tiers").forGetter(SkillData::getAllNodesByTier),
            EquipmentSlot.CODEC.fieldOf("equipmentSlot").forGetter(SkillData::getEquipmentSlot)
    ).apply(instance, SkillData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillData> STREAM_CODEC = StreamCodec.composite(
            SkillDataNode.STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()), SkillData::getAllNodesByTier,
            EquipmentSlot.STREAM_CODEC, SkillData::getEquipmentSlot,
            SkillData::new);

    public static Builder builder(EquipmentSlot slot) {
        return new Builder(slot);
    }

    public static class Builder {
        private final List<List<SkillDataNode>> nodes;
        private final EquipmentSlot equipmentSlot;
        private List<SkillDataNode> currentTier;
        private List<SkillDataNode> previousTier;
        private SkillDataNode currentNode = null;
        private boolean including = true;

        private Builder(EquipmentSlot slot) {
            nodes = new ArrayList<>();
            equipmentSlot = slot;
        }

        public Builder tier() {
            previousTier = currentTier;
            currentTier = new ArrayList<>();
            nodes.add(currentTier);
            currentNode = null;
            return this;
        }

        public Builder attributeNode(int id, String name, String description, ResourceLocation attribute, float value) {
            return attributeNode(id, name, description, List.of(attribute), value);
        }

        public Builder attributeNode(int id, String name, String description, List<ResourceLocation> attributes, float value) {
            if (including) {
                currentNode = new AttributeNode(id, name, description, 1, attributes, value, new ArrayList<>(), Optional.empty());
                currentTier.add(currentNode);
            }

            return this;
        }

        public Builder infiniteAttributeNode(int id, String name, String description, ResourceLocation attribute, float value) {
            return infiniteAttributeNode(id, name, description, List.of(attribute), value);
        }

        public Builder infiniteAttributeNode(int id, String name, String description, List<ResourceLocation> attributes, float value) {
            if (including) {
                currentNode = new AttributeNode(id, name, description, 0, attributes, value, new ArrayList<>(), Optional.empty());
                currentTier.add(currentNode);
            }

            return this;
        }

        public Builder dataComponentNode(int id, String name, String description, ResourceLocation dataComponent, float value) {
            return dataComponentNode(id, name, description, dataComponent, value, 1);
        }

        public Builder dataComponentNode(int id, String name, String description, ResourceLocation dataComponent, float value, int limit) {
            if (including) {
                currentNode = new DataComponentNode(id, name, description, limit, dataComponent, value, new ArrayList<>(), Optional.empty());
                currentTier.add(currentNode);
            }

            return this;
        }

        public Builder infiniteDataComponentNode(int id, String name, String description, ResourceLocation dataComponent, float value) {
            return dataComponentNode(id, name, description, dataComponent, value, 0);
        }

        public Builder enchantmentNode(int id, String name, String description, ResourceKey<Enchantment> enchantment, int level) {
            if (including) {
                currentNode = new EnchantmentNode(id, name, description, enchantment.location(), level, new ArrayList<>(), Optional.empty());
                currentTier.add(currentNode);
            }

            return this;
        }

        public Builder nutrition(int id, int level, int nutrition, String description) {
            return nutrition(id, level, nutrition, description, 1);
        }

        public Builder nutrition(int id, int level, int nutrition, String description, int limit) {
            if (including) {
                currentNode = new FoodDataComponentNode(id, SkillTreeTitles.nutrition(level), description, limit,
                        new FoodProperties(nutrition, 0, false), new ArrayList<>(), Optional.empty());
                currentTier.add(currentNode);
            }

            return this;
        }

        public Builder saturation(int id, int level, float saturation, String description) {
            return saturation(id, level, saturation, description, 1);
        }

        public Builder saturation(int id, int level, float saturation, String description, int limit) {
            if (including) {
                currentNode = new FoodDataComponentNode(id, SkillTreeTitles.saturation(level), description, limit,
                        new FoodProperties(0, saturation, false), new ArrayList<>(), Optional.empty());
                currentTier.add(currentNode);
            }

            return this;
        }

        public Builder alwaysEat(int id, String description) {
            if (including) {
                currentNode = new FoodDataComponentNode(id, SkillTreeTitles.ALWAYS_EAT, description, 1,
                        new FoodProperties(0, 0, true), new ArrayList<>(), Optional.empty());
                currentTier.add(currentNode);
            }

            return this;
        }

        public Builder effect(int id, SkillTreeDescriptions description, MobEffectInstance effectInstance) {
             if (including) {
                 String effectName = effectInstance.getEffect().value().getDisplayName().getString();
                currentNode = new EffectNode(id, effectName, description.effect(effectName, effectInstance.getDuration()), 0, new ArrayList<>(),
                        Optional.of(new SkillSubText(SkillTreeTitles.EFFECT_SUB_TEXT, "#ABABAB")), effectInstance);
                currentTier.add(currentNode);
            }

            return this;
        }

        public Builder subText(String subtext, String color) {
            if (including) {
                if (currentNode == null) {
                    throw new IllegalArgumentException("Cannot add subtext with no node!");
                }

                currentNode.setSubtext(new SkillSubText(subtext, color));
            }

            return this;
        }

        public Builder nodeRequirement(int... nodes) {
            if (including) {
                if (currentNode == null) {
                    throw new IllegalArgumentException("Cannot add requirements with no node!");
                }

                currentNode.addRequirement(new NodeSkillDataRequirement(Arrays.stream(nodes).boxed().toList()));
            }

            return this;
        }

        public Builder previousTierOrRequirements() {
            if (including) {
                if (previousTier == null) {
                    throw new IllegalArgumentException("Cannot add previous tier or requirements with no previous tier!");
                }

                currentNode.addRequirement(new NodeOrSkillDataRequirement(previousTier.stream()
                        .filter(n -> !n.getDescription().equals(currentNode.getDescription()))
                        .map(SkillDataNode::getId)
                        .toList()));
            }

            return this;
        }

        public Builder notNodeRequirement(int notNode, int unlessNode) {
            if (including) {
                currentNode.addRequirement(new NotNodeSkillDataRequirement(List.of(notNode), List.of(unlessNode)));
            }

            return this;
        }

        public Builder itemRequirement(Item... items) {
            if (including) {
                currentNode.addRequirement(new SkillItemRequirement(Arrays.stream(items).toList()));
            }

            return this;
        }

        public Builder optional(boolean include) {
            this.including = include;
            return this;
        }

        public Builder endOptional() {
            including = true;
            return this;
        }

        public SkillData build() {
            return new SkillData(nodes, equipmentSlot);
        }
    }
}
