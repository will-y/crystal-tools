package dev.willyelton.crystal_tools.common.levelable.skill.requirement;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.common.levelable.skill.node.SkillDataNode;
import dev.willyelton.crystal_tools.utils.CodecUtils;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NodeOrSkillDataRequirement implements SkillDataRequirement, SkillDataNodeRequirement {
    List<Integer> nodes;

    public NodeOrSkillDataRequirement(List<Integer> nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean canLevel(SkillPoints points, Player player) {
        for (Integer requiredNode : nodes) {
            int pointsInNode = points.getPoints(requiredNode);

            if (pointsInNode > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<Integer> getRequiredNodes() {
        return nodes;
    }

    @Override
    public RequirementType getRequirementType() {
        return RequirementType.NODE_OR;
    }

    @Override
    public JsonElement toJson() {
        return CodecUtils.encodeOrThrow(CODEC, this);
    }

    public static final Codec<NodeOrSkillDataRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.listOf().fieldOf("or_node").forGetter(NodeOrSkillDataRequirement::getRequiredNodes)
    ).apply(instance, NodeOrSkillDataRequirement::new));
}
