package dev.willyelton.crystal_tools.tool.skills;

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
}
