package dev.willyelton.crystal_tools.crafting;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CrystalTools.MODID);

    public static final RegistryObject<SimpleCraftingRecipeSerializer<CrystalElytraRecipe>> CRYSTAL_ELYTRA_RECIPE = RECIPES.register("crystal_elytra_recipe", () -> new SimpleCraftingRecipeSerializer<>(CrystalElytraRecipe::new));
    public static final RegistryObject<SimpleCraftingRecipeSerializer<CrystalAIOTRecipe>> CRYSTAL_AIOT_RECIPE = RECIPES.register("crystal_aiot_recipe", () -> new SimpleCraftingRecipeSerializer<>(CrystalAIOTRecipe::new));

    public static void initRecipes() {
        RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
