package dev.willyelton.crystal_tools.compat.jei;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.crafting.CrystalToolsRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@JeiPlugin
public class CrystalToolsJeiPlugin implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CrystalTools.MODID, "jeiplugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(CrystalToolsRecipe.class, CrystalToolsCraftingCategoryExtension::new);
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Registration.CRYSTAL_FURNACE_ITEM.get()), RecipeTypes.SMELTING);
    }
}