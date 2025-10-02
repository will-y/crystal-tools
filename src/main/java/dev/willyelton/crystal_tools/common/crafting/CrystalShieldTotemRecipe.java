package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Stream;

import static dev.willyelton.crystal_tools.ModRegistration.CRYSTAL_SHIELD_TOTEM_RECIPE;

public class CrystalShieldTotemRecipe extends CrystalToolsRecipe {
    public CrystalShieldTotemRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public List<ItemStack> getInputs() {
        return Stream.of(ModRegistration.CRYSTAL_SHIELD.get(), Items.TOTEM_OF_UNDYING)
                .map(ItemStack::new)
                .toList();
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(ModRegistration.CRYSTAL_SHIELD);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.size() != 2) return false;

        return (shieldMatches(input.getItem(0)) && input.getItem(1).is(Items.TOTEM_OF_UNDYING)) ||
                (shieldMatches(input.getItem(1)) && input.getItem(0).is(Items.TOTEM_OF_UNDYING));
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = (input.getItem(0).is(ModRegistration.CRYSTAL_SHIELD) ? input.getItem(0) : input.getItem(1)).copy();

        DataComponents.addToComponent(result, DataComponents.FILLED_TOTEM_SLOTS, 1);

        return result;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return CRYSTAL_SHIELD_TOTEM_RECIPE.get();
    }

    private boolean shieldMatches(ItemStack stack) {
        if (!stack.is(ModRegistration.CRYSTAL_SHIELD)) return false;
        int totemSlots = stack.getOrDefault(DataComponents.TOTEM_SLOTS, 0);
        int filledSlots = stack.getOrDefault(DataComponents.FILLED_TOTEM_SLOTS, 0);

        return totemSlots - filledSlots > 0;
    }
}
