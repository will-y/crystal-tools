package dev.willyelton.crystal_tools.common.levelable.skill.node;

import dev.willyelton.crystal_tools.common.levelable.skill.SkillData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

public interface ItemStackNode {
    void processNode(SkillData skillData, ItemStack stack, int pointsToSpend, RegistryAccess registryAccess);
}
