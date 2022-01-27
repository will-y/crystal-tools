package dev.willyelton.crystal_tools.item.skill.requirement;

import dev.willyelton.crystal_tools.item.skill.SkillDataNode;
import dev.willyelton.crystal_tools.item.skill.SkillData;
import dev.willyelton.crystal_tools.utils.ArrayUtils;

import java.util.List;

public class NodeSkillDataRequirement extends SkillDataRequirement {
    int[] requiredNodes;
    boolean inverse;

    public NodeSkillDataRequirement(int[] requiredNodes) {
        this(requiredNodes, false);
    }

    public NodeSkillDataRequirement(int[] requiredNodes, boolean inverse) {
        this.requiredNodes = requiredNodes;
        this.inverse = inverse;
    }

    @Override
    public boolean canLevel(SkillData data) {
        List<SkillDataNode> nodes = data.getAllNodes();
        for (SkillDataNode node : nodes) {
            if (ArrayUtils.arrayContains(requiredNodes, node.getId())) {
                if ((!inverse && node.getPoints() == 0) || (inverse && node.getPoints() > 0)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int[] getRequiredNodes() {
        return requiredNodes;
    }

    @Override
    public RequirementType getRequirementType() {
        return inverse ? RequirementType.NODE_NOT : RequirementType.NODE_AND;
    }
}
