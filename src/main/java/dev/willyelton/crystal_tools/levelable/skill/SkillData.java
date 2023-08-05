package dev.willyelton.crystal_tools.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.utils.ListUtils;

import java.util.List;

public class SkillData {
    private final List<List<SkillDataNode>> nodes;
    // Flattened nodes, calculated lazily
    private List<SkillDataNode> flatNodes = null;

    private SkillData(List<List<SkillDataNode>> nodes) {
        this.nodes = nodes;
    }

    public void applyPoints(int[] points) {
        List<SkillDataNode> nodes = getAllNodes();

        for (SkillDataNode node : nodes) {
            node.setPoints(points[node.getId()]);
        }
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

    public static final Codec<SkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        SkillDataNode.CODEC.listOf().listOf().fieldOf("tiers").forGetter(SkillData::getAllNodesByTier)
    ).apply(instance, SkillData::new));
}
