package dev.willyelton.crystal.tools.common.compat.jei.pedestal;

import dev.willyelton.crystal.tools.common.datamap.ActionData;
import dev.willyelton.crystal.tools.common.datamap.DataMaps;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record PedestalRecipe(ItemStack stack, ActionData data) {
    public static List<PedestalRecipe> createPedestalRecipes(IIngredientManager ingredientManager) {
        List<PedestalRecipe> recipes = new ArrayList<>();

        for (ItemStack stack : ingredientManager.getAllItemStacks()) {
            Holder<Item> holder = stack.typeHolder();

            ActionData data = holder.getData(DataMaps.PEDESTAL_ACTIONS);
            if (data != null) {
                recipes.add(new PedestalRecipe(stack, data));
            }
        }

        return recipes;
    }
}
