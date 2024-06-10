package dev.willyelton.crystal_tools.common.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.utils.ListUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;
import java.util.List;

public class SkillData {
    private final List<List<SkillDataNode>> nodes;
    // Flattened nodes, calculated lazily
    private List<SkillDataNode> flatNodes = null;
    private int totalPoints;

    private SkillData(List<List<SkillDataNode>> nodes) {
        this.nodes = nodes;
    }

    public void applyPoints(int[] points) {
        List<SkillDataNode> nodes = getAllNodes();

        for (SkillDataNode node : nodes) {
            node.setPoints(points[node.getId()]);
        }

        this.totalPoints = Arrays.stream(points).sum();
    }

    public void applyPoints(List<Integer> points) {
        List<SkillDataNode> nodes = getAllNodes();

        // TODO: Can probably short circuit here if I need to
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
        SkillDataNode.CODEC.listOf().listOf().fieldOf("tiers").forGetter(SkillData::getAllNodesByTier)
    ).apply(instance, SkillData::new));
    // TODO: Probably better to write from scratch I think, not too important because only fires on datapack load
    public static final StreamCodec<ByteBuf, SkillData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
}
