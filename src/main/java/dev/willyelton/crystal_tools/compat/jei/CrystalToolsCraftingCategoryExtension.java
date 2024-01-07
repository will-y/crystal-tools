
package dev.willyelton.crystal_tools.compat.jei;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.crafting.CrystalToolsRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CrystalToolsCraftingCategoryExtension implements ICraftingCategoryExtension {
    private final CrystalToolsRecipe recipe;

    public CrystalToolsCraftingCategoryExtension(CrystalToolsRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(CrystalTools.MODID, "crystal_aiot");
    }

    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, @NotNull IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.getInputs().stream().map(List::of).toList();
        int width = 3;
        int height = 3;
        craftingGridHelper.createAndSetOutputs(builder, List.of(recipe.getOutput()));
        craftingGridHelper.createAndSetInputs(builder, inputs, width, height);
    }
}