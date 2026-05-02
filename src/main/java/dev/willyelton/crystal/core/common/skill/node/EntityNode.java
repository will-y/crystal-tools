package dev.willyelton.crystal.core.common.skill.node;

import dev.willyelton.crystal.core.common.skill.SkillData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.LivingEntity;

public interface EntityNode {
    void processNode(SkillData skillData, LivingEntity entity, int pointsToSpend, RegistryAccess registryAccess);
}
