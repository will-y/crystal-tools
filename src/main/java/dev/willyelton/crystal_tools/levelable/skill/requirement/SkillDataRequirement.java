package dev.willyelton.crystal_tools.levelable.skill.requirement;

import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import net.minecraft.world.entity.player.Player;

public interface SkillDataRequirement {
    boolean canLevel(SkillData data, Player player);

    RequirementType getRequirementType();
}
