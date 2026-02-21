package dev.willyelton.crystal_tools.common.compat.jei;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.compat.jei.generator.GeneratorRecipe;
import dev.willyelton.crystal_tools.common.compat.jei.generator.GeneratorRecipeCategory;
import dev.willyelton.crystal_tools.common.compat.jei.pedestal.PedestalRecipe;
import dev.willyelton.crystal_tools.common.compat.jei.pedestal.PedestalRecipeCategory;
import dev.willyelton.crystal_tools.common.crafting.CrystalToolsRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

@JeiPlugin
public class CrystalToolsJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return rl("jeiplugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(CrystalToolsRecipe.class, new CrystalToolsCraftingCategoryExtension());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(RecipeTypes.SMELTING, new ItemStack(ModRegistration.CRYSTAL_FURNACE_ITEM.get()));
        registration.addCraftingStation(CrystalToolsRecipeTypes.GENERATOR, new ItemStack(ModRegistration.CRYSTAL_GENERATOR.get()));
        registration.addCraftingStation(CrystalToolsRecipeTypes.PEDESTAL, new ItemStack(ModRegistration.CRYSTAL_PEDESTAL.get()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new GeneratorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new PedestalRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CrystalToolsRecipeTypes.GENERATOR, GeneratorRecipe.createGeneratorRecipes(registration.getIngredientManager()));
        registration.addRecipes(CrystalToolsRecipeTypes.PEDESTAL, PedestalRecipe.createPedestalRecipes(registration.getIngredientManager()));
    }
}