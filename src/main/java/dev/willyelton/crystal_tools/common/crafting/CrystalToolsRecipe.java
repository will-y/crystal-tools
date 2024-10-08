package dev.willyelton.crystal_tools.common.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;

import java.util.List;

public abstract class CrystalToolsRecipe extends CustomRecipe {
    public CrystalToolsRecipe(CraftingBookCategory category) {
        super(category);
    }

    public abstract List<ItemStack> getInputs();

    public abstract ItemStack getOutput();
}
