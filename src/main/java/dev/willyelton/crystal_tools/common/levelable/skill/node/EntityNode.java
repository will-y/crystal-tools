package dev.willyelton.crystal_tools.common.levelable.skill.node;

import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.LivingEntity;

public interface EntityNode {
    void processNode(SkillData skillData, LivingEntity entity, int pointsToSpend, RegistryAccess registryAccess);
}
