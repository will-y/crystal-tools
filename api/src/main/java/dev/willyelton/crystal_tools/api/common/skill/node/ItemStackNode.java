package dev.willyelton.crystal_tools.api.common.skill.node;

import dev.willyelton.crystal_tools.api.common.skill.SkillData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

public interface ItemStackNode {
    void processNode(SkillData skillData, ItemStack stack, int pointsToSpend, RegistryAccess registryAccess);
}
