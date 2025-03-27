package dev.willyelton.crystal_tools.common.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.node.DataComponentSkillNode;
import dev.willyelton.crystal_tools.common.levelable.skill.node.EnchantmentDataNode;
import dev.willyelton.crystal_tools.common.levelable.skill.node.AttributeSkillDataNode;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.NodeOrSkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.NodeSkillDataRequirement;
import dev.willyelton.crystal_tools.common.levelable.skill.requirement.NotNodeSkillDataRequirement;
import dev.willyelton.crystal_tools.utils.ListUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SkillData {
    private final List<List<SkillDataNode>> nodes;
    // Flattened nodes, calculated lazily
    private List<SkillDataNode> flatNodes = null;
    private int totalPoints;

    private SkillData(List<List<SkillDataNode>> nodes) {
        this.nodes = nodes;
    }

    public void applyPoints(int[] points) {
        List<? extends SkillDataNode> nodes = getAllNodes();

        for (SkillDataNode node : nodes) {
            node.setPoints(points[node.getId()]);
        }

        this.totalPoints = Arrays.stream(points).sum();
    }

    public void applyPoints(List<Integer> points) {
        List<? extends SkillDataNode> nodes = getAllNodes();

        for (SkillDataNode node : nodes) {
            if (points.size() > node.getId()) {
                node.setPoints(points.get(node.getId()));
            } else {
                node.setPoints(0);
            }
        }

        this.totalPoints = points.stream().mapToInt(Integer::intValue).sum();
    }

    public List<List<SkillDataNode>> getAllNodesByTier() {
        return this.nodes;
    }

    public List<SkillDataNode> getAllNodes() {
        if (flatNodes == null) {
            flatNodes = ListUtils.flattenList(nodes);
        }

        return flatNodes;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    // TODO: This should be temporary, maybe include this whole class in the 1.21 redesign. Might not be included in CODECS, check for sync issues
    public void addPoint() {
        this.totalPoints++;
    }

    public static final Codec<SkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillDataNode.CODEC.listOf().listOf()
                    .fieldOf("tiers").forGetter(SkillData::getAllNodesByTier)
    ).apply(instance, SkillData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillData> STREAM_CODEC = StreamCodec.composite(
            SkillDataNode.STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()), SkillData::getAllNodesByTier,
            SkillData::new);

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<List<SkillDataNode>> nodes;
        private List<SkillDataNode> currentTier;
        private List<SkillDataNode> previousTier;
        private SkillDataNode currentNode = null;

        private Builder() {
            nodes = new ArrayList<>();
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
            currentNode = new AttributeSkillDataNode(id, name, description, 1, attributes, value, new ArrayList<>(), Optional.empty());
            currentTier.add(currentNode);
            return this;
        }

        public Builder dataComponentNode(int id, String name, String description, ResourceLocation dataComponent, float value) {
            currentNode = new DataComponentSkillNode(id, name, description, 1, dataComponent, value, new ArrayList<>(), Optional.empty());
            currentTier.add(currentNode);
            return this;
        }

        public Builder enchantmentNode(int id, String name, String description, ResourceKey<Enchantment> enchantment, int level) {
            currentNode = new EnchantmentDataNode(id, name, description, enchantment.location(), level, new ArrayList<>(), Optional.empty());
            currentTier.add(currentNode);
            return this;
        }

        public Builder nodeRequirement(int id) {
            if (currentNode == null) {
                throw new IllegalArgumentException("Cannot add requirements with no node!");
            }

            currentNode.addRequirement(new NodeSkillDataRequirement(List.of(id)));
            return this;
        }

        public Builder orNodeRequirement(int... nodes) {
            if (currentNode == null) {
                throw new IllegalArgumentException("Cannot add requirements with no node!");
            }

            currentNode.addRequirement(new NodeOrSkillDataRequirement(Arrays.stream(nodes).boxed().toList()));
            return this;
        }

        public Builder previousTierOrRequirements() {
            if (previousTier == null) {
                throw new IllegalArgumentException("Cannot add previous tier or requirements with no previous tier!");
            }

            currentNode.addRequirement(new NodeOrSkillDataRequirement(previousTier.stream()
                    .filter(n -> !n.getDescription().equals(currentNode.getDescription()))
                    .map(SkillDataNode::getId)
                    .toList()));

            return this;
        }

        public Builder notNodeRequirement(int notNode, int unlessNode) {
            currentNode.addRequirement(new NotNodeSkillDataRequirement(List.of(notNode), List.of(unlessNode)));
            return this;
        }

        public SkillData build() {
            return new SkillData(nodes);
        }
    }
}
