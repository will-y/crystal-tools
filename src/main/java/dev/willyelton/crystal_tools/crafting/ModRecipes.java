package dev.willyelton.crystal_tools.crafting;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CrystalTools.MODID);
    public static final RegistryObject<SimpleRecipeSerializer<CrystalElytraRecipe>> CRYSTAL_ELYTRA_RECIPE = RECIPES.register("crystal_elytra_recipe", () -> new SimpleRecipeSerializer<>(CrystalElytraRecipe::new));

    public static void initRecipes() {
        RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}