package dev.willyelton.crystal_tools.levelable.skill.requirement;

import dev.willyelton.crystal_tools.levelable.skill.SkillDataNode;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NodeSkillDataRequirement implements SkillDataRequirement, SkillDataNodeRequirement {
    int[] requiredNodes;
    boolean inverse;
    int[] unless;

    public NodeSkillDataRequirement(int[] requiredNodes) {
        this(requiredNodes, false);
    }

    public NodeSkillDataRequirement(int[] requiredNodes, boolean inverse) {
        this(requiredNodes, inverse, new int[] {});
    }

    public NodeSkillDataRequirement(int[] requiredNodes, boolean inverse, int[] unless) {
        this.requiredNodes = requiredNodes;
        this.inverse = inverse;
        this.unless = unless;
    }

    @Override
    public boolean canLevel(SkillData data, Player player) {
        boolean toReturn = true;
        List<SkillDataNode> nodes = data.getAllNodes();
        for (SkillDataNode node : nodes) {
            if (ArrayUtils.arrayContains(requiredNodes, node.getId())) {
                if ((!inverse && node.getPoints() == 0) || (inverse && node.getPoints() > 0)) {
                    toReturn = false;
                }
            }

            if (inverse && ArrayUtils.arrayContains(unless, node.getId()) & node.getPoints() > 0) {
                return true;
            }
        }
        return toReturn;
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
