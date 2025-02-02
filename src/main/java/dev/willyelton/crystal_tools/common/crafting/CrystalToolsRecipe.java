package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;

import java.util.Collections;
import java.util.List;

public abstract class CrystalToolsRecipe extends CustomRecipe {
    public CrystalToolsRecipe(CraftingBookCategory category) {
        super(category);
    }

    public abstract List<ItemStack> getInputs();

    public abstract ItemStack getOutput();

    protected int getPoints(ItemStack stack) {
        List<Integer> points = stack.getOrDefault(DataComponents.POINTS_ARRAY, Collections.emptyList());
        return points.stream().mapToInt(Integer::intValue).sum() + stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
    }
}
