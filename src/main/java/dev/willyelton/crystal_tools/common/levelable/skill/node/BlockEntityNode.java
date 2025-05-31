package dev.willyelton.crystal_tools.common.levelable.skill.node;

import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.core.RegistryAccess;

public interface BlockEntityNode {
    void processNode(SkillData skillData, LevelableBlockEntity blockEntity, int pointsToSpend, RegistryAccess registryAccess);
}
