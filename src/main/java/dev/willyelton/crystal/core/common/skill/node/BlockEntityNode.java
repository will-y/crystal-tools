package dev.willyelton.crystal.core.common.skill.node;

import dev.willyelton.crystal.core.common.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal.core.common.skill.SkillData;
import net.minecraft.core.RegistryAccess;

public interface BlockEntityNode {
    void processNode(SkillData skillData, LevelableBlockEntity blockEntity, int pointsToSpend, RegistryAccess registryAccess);
}
