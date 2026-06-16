package dev.willyelton.crystal.core.common.skill.requirement;

import com.mojang.serialization.MapCodec;
import dev.willyelton.crystal.core.common.skill.SkillPoints;
import net.minecraft.world.entity.player.Player;

public interface SkillDataRequirement {
    boolean canLevel(SkillPoints points, Player player);

    RequirementType getRequirementType();

    MapCodec<? extends SkillDataRequirement> codec();
}
