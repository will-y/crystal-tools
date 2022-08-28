package dev.willyelton.crystal_tools.item.skill.requirement;

import dev.willyelton.crystal_tools.item.skill.SkillDataNode;
import dev.willyelton.crystal_tools.item.skill.SkillData;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NodeOrSkillDataRequirement implements SkillDataRequirement, SkillDataNodeRequirement {
    int[] nodes;

    public NodeOrSkillDataRequirement(int[] nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean canLevel(SkillData data, Player player) {
        List<SkillDataNode> nodes = data.getAllNodes();
        for (SkillDataNode node : nodes) {
            if (ArrayUtils.arrayContains(this.nodes, node.getId())) {
                if (node.getPoints() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int[] getRequiredNodes() {
        return nodes;
    }

    @Override
    public RequirementType getRequirementType() {
        return RequirementType.NODE_OR;
    }
}
