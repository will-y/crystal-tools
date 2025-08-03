
package dev.willyelton.crystal_tools.common.compat.jei;

import dev.willyelton.crystal_tools.common.crafting.CrystalToolsRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Optional;

public class CrystalToolsCraftingCategoryExtension implements ICraftingCategoryExtension<CrystalToolsRecipe> {

    @Override
    public Optional<ResourceLocation> getRegistryName(RecipeHolder<CrystalToolsRecipe> recipe) {
        return Optional.of(recipe.id());
    }

    @Override
    public void setRecipe(RecipeHolder<CrystalToolsRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipeHolder.value().getInputs().stream().map(List::of).toList();
        craftingGridHelper.createAndSetOutputs(builder, List.of(recipeHolder.value().getOutput()));
        craftingGridHelper.createAndSetInputs(builder, inputs, 3, 3);
    }
}