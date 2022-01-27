package dev.willyelton.crystal_tools.item.skill.requirement;

import dev.willyelton.crystal_tools.item.skill.SkillData;

public abstract class SkillDataRequirement {
    public abstract boolean canLevel(SkillData data);

    public abstract int[] getRequiredNodes();

    public abstract RequirementType getRequirementType();
}
