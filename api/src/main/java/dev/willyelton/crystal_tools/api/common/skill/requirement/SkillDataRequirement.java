package dev.willyelton.crystal_tools.api.common.skill.requirement;

import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal_tools.api.common.skill.SkillPoints;
import net.minecraft.world.entity.player.Player;

public interface SkillDataRequirement {
    boolean canLevel(SkillPoints points, Player player);

    RequirementType getRequirementType();

    MapCodec<? extends SkillDataRequirement> codec();
}
