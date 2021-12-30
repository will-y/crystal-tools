package dev.willyelton.crystal_tools.tool.skill.requirement;

import dev.willyelton.crystal_tools.tool.skill.SkillData;
import dev.willyelton.crystal_tools.tool.skill.SkillDataNode;
import dev.willyelton.crystal_tools.utils.ArrayUtils;

import java.util.List;

public class NodeOrSkillDataRequirement extends SkillDataRequirement {
    int[] nodes;

    public NodeOrSkillDataRequirement(int[] nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean canLevel(SkillData data) {
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
