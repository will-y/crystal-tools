package dev.willyelton.crystal_tools.levelable.skill.requirement;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import dev.willyelton.crystal_tools.utils.CodecUtils;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NodeOrSkillDataRequirement implements SkillDataRequirement, SkillDataNodeRequirement {
    List<Integer>nodes;

    public NodeOrSkillDataRequirement(List<Integer> nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean canLevel(SkillData data, Player player) {
        List<SkillDataNode> nodes = data.getAllNodes();
        for (SkillDataNode node : nodes) {
            if (this.nodes.contains(node.getId())) {
                if (node.getPoints() > 0) {
                    return true;
                }
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
