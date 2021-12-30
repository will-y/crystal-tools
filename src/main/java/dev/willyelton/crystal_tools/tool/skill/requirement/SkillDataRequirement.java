package dev.willyelton.crystal_tools.tool.skill.requirement;

import dev.willyelton.crystal_tools.tool.skill.SkillData;

public abstract class SkillDataRequirement {
    public abstract boolean canLevel(SkillData data);

    public abstract int[] getRequiredNodes();

    public abstract RequirementType getRequirementType();
}
