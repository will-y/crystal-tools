package dev.willyelton.crystal_tools.api.common.skill.node;

import dev.willyelton.crystal_tools.api.common.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.api.common.skill.SkillData;
import net.minecraft.core.RegistryAccess;

public interface BlockEntityNode {
    void processNode(SkillData skillData, LevelableBlockEntity blockEntity, int pointsToSpend, RegistryAccess registryAccess);
}
