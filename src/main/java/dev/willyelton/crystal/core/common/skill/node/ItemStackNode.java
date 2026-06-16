package dev.willyelton.crystal.core.common.skill.node;

import dev.willyelton.crystal.core.common.skill.SkillData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

public interface ItemStackNode {
    void processNode(SkillData skillData, ItemStack stack, int pointsToSpend, RegistryAccess registryAccess);
}
