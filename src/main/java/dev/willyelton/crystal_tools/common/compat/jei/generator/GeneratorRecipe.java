package dev.willyelton.crystal_tools.common.compat.jei.generator;

import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.datamap.GeneratorFuelData;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.core.Holder;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record GeneratorRecipe(ItemStack stack, int burnTime, int bonusGeneration, String upgradeRequired) {
    public static List<GeneratorRecipe> createGeneratorRecipes(IIngredientManager ingredientManager) {
        List<GeneratorRecipe> recipes = new ArrayList<>();
        for (ItemStack stack : ingredientManager.getAllItemStacks()) {
            Holder<Item> holder = stack.getItemHolder();
            GeneratorFuelData gemFuelData = holder.getData(DataMaps.GENERATOR_GEMS);
            if (gemFuelData != null) {
                recipes.add(new GeneratorRecipe(stack, gemFuelData.burnTime(), gemFuelData.bonusGeneration(), "Gem Generator"));
                continue;
            }

            GeneratorFuelData metalFuelData = holder.getData(DataMaps.GENERATOR_METALS);
            if (metalFuelData != null) {
                recipes.add(new GeneratorRecipe(stack, metalFuelData.burnTime(), metalFuelData.bonusGeneration(), "Metal Generator"));
                continue;
            }

            FoodProperties foodProperties = stack.getFoodProperties(null);
            if (foodProperties != null) {
                int burnTime = CrystalGeneratorBlockEntity.getBurnTimeFromFood(foodProperties);

                if (burnTime > 0) {
                    recipes.add(new GeneratorRecipe(stack, burnTime, 0, "Food Generator"));
                    continue;
                }
            }

            int burnTime = stack.getBurnTime(null);
            if (burnTime != 0) {
                recipes.add(new GeneratorRecipe(stack, burnTime, 0, ""));
            }
        }

        recipes.sort(Comparator.comparing(GeneratorRecipe::upgradeRequired).thenComparing(GeneratorRecipe::bonusGeneration).thenComparing(GeneratorRecipe::burnTime));

        return recipes;
    }
}
