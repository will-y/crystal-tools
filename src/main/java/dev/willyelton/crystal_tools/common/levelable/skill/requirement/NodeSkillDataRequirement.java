package dev.willyelton.crystal_tools.common.levelable.skill.requirement;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.utils.CodecUtils;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NodeSkillDataRequirement implements SkillDataRequirement, SkillDataNodeRequirement {
    List<Integer> requiredNodes;
    boolean inverse;
    List<Integer> unless;

    public NodeSkillDataRequirement(List<Integer> requiredNodes) {
        this(requiredNodes, false);
    }

    /**
     * Because of codecs, these should only be called by subclasses
     */
    protected NodeSkillDataRequirement(List<Integer> requiredNodes, boolean inverse) {
        this(requiredNodes, inverse, List.of());
    }

    /**
     * Because of codecs, these should only be called by subclasses
     */
    protected NodeSkillDataRequirement(List<Integer> requiredNodes, boolean inverse, List<Integer> unless) {
        this.requiredNodes = requiredNodes;
        this.inverse = inverse;
        this.unless = unless;
    }

    @Override
    public boolean canLevel(SkillPoints points, Player player) {
        if (unlessActive(points)) return true;

        for (Integer node : requiredNodes) {
            int pointsInNode = points.getPoints(node);

            if ((!inverse && pointsInNode == 0) || (inverse && pointsInNode > 0)) {
                return false;
            }
        }

        return true;
    }

    private boolean unlessActive(SkillPoints points) {
        if (inverse) {
            for (Integer node : unless) {
                if (points.getPoints(node) == 0) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public List<Integer> getRequiredNodes() {
        return requiredNodes;
    }

    protected List<Integer> getUnless() {
        return unless;
    }

    @Override
    public RequirementType getRequirementType() {
        return inverse ? RequirementType.NODE_NOT : RequirementType.NODE_AND;
    }

    @Override
    public JsonElement toJson() {
        return CodecUtils.encodeOrThrow(CODEC, this);
    }

    public static final Codec<NodeSkillDataRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.listOf().fieldOf("node").forGetter(NodeSkillDataRequirement::getRequiredNodes)
    ).apply(instance, NodeSkillDataRequirement::new));
}
