package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

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

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(getIngredients());
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapelessCraftingRecipeDisplay(getIngredients().stream().map(Ingredient::display).toList(),
                new SlotDisplay.ItemStackSlotDisplay(getOutput()), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    protected List<Ingredient> getIngredients() {
        return getInputs().stream().map(ItemStack::getItem).map(Ingredient::of).toList();
    }
}
