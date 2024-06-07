package dev.willyelton.crystal_tools.crafting;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// TODO: Maybe move to registration
public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(Registries.RECIPE_SERIALIZER, CrystalTools.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<CrystalElytraRecipe>> CRYSTAL_ELYTRA_RECIPE = RECIPES.register("crystal_elytra_recipe", () -> new SimpleCraftingRecipeSerializer<>(CrystalElytraRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<CrystalAIOTRecipe>> CRYSTAL_AIOT_RECIPE = RECIPES.register("crystal_aiot_recipe", () -> new SimpleCraftingRecipeSerializer<>(CrystalAIOTRecipe::new));

    public static void initRecipes(IEventBus eventBus) {
        RECIPES.register(eventBus);
    }
}
