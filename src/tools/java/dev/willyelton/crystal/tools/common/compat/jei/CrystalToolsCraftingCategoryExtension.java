
package dev.willyelton.crystal.tools.common.compat.jei;

import dev.willyelton.crystal.tools.common.crafting.CrystalToolsRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.stream.Collectors;

public class CrystalToolsCraftingCategoryExtension implements ICraftingCategoryExtension<CrystalToolsRecipe> {

    @Override
    public List<SlotDisplay> getIngredients(RecipeHolder<CrystalToolsRecipe> recipeHolder) {
        return recipeHolder.value().getInputs().stream().map(stack -> new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack))).collect(Collectors.toList());
    }

    @Override
    public void setRecipe(RecipeHolder<CrystalToolsRecipe> recipeHolder, IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipeHolder.value().getInputs().stream().map(List::of).toList();
        craftingGridHelper.createAndSetOutputs(builder, List.of(recipeHolder.value().getOutput().create()));
        craftingGridHelper.createAndSetInputs(builder, inputs, 3, 3);
    }
}