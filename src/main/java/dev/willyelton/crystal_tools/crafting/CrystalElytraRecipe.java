package dev.willyelton.crystal_tools.crafting;

import dev.willyelton.crystal_tools.item.armor.ModArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CrystalElytraRecipe extends CustomRecipe {
    public CrystalElytraRecipe(ResourceLocation pId) {
        super(pId);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
        ItemStack elytraItem = ItemStack.EMPTY;
        ItemStack crystalChestPlateItem = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);

            if (itemStack.is(ModArmor.CRYSTAL_CHESTPLATE.get())) {
                crystalChestPlateItem = itemStack;
            } else if (itemStack.is(Items.ELYTRA)) {
                elytraItem = itemStack;
            }
        }

        return !elytraItem.isEmpty() && !crystalChestPlateItem.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container) {
        return new ItemStack(ModArmor.CRYSTAL_ELYTRA.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.CRYSTAL_ELYTRA_RECIPE.get();
    }
}
