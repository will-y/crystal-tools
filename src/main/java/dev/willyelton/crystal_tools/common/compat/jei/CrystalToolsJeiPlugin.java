package dev.willyelton.crystal_tools.common.compat.jei;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class CrystalToolsJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "jeiplugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(CrystalToolsRecipe.class, new CrystalToolsCraftingCategoryExtension());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Registration.CRYSTAL_FURNACE_ITEM.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(Registration.CRYSTAL_GENERATOR.get()), CrystalToolsRecipeTypes.GENERATOR);
        registration.addRecipeCatalyst(new ItemStack(Registration.CRYSTAL_PEDESTAL.get()), CrystalToolsRecipeTypes.PEDESTAL);
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