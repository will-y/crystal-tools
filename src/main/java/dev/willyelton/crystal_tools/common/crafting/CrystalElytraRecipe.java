package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class CrystalElytraRecipe extends CrystalToolsRecipe {
    public CrystalElytraRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        if (container.size() < 2) return false;

        boolean foundElytra = false;
        boolean foundChestplate = false;

        for (int i = 0; i < container.size(); i++) {
            ItemStack itemStack = container.getItem(i);

            if (itemStack.is(ModRegistration.CRYSTAL_CHESTPLATE.get())) {
                if (foundChestplate)
                    return false;
                foundChestplate = true;
            } else if (itemStack.is(Items.ELYTRA)) {
                if (foundElytra)
                    return false;
                foundElytra = true;
            } else if (!itemStack.isEmpty()) {
                return false;
            }
        }

        return foundChestplate && foundElytra;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registryAccess) {
        ItemStack stack = new ItemStack(ModRegistration.CRYSTAL_ELYTRA.get());
        List<ItemStack> items = this.getItems(container);

        ItemStack elytraItem = items.get(0);
        ItemStack crystalChestPlateItem = items.get(1);

        if (crystalChestPlateItem.isEmpty()) {
            return crystalChestPlateItem;
        }

        // points from enchantments on elytra
        int enchantmentPoints = EnchantmentUtils.pointsFromEnchantments(elytraItem);

        int totalPoints = getPoints(stack) + enchantmentPoints;

        // Set skill points
        stack.set(DataComponents.SKILL_POINTS, totalPoints);

        stack.set(DataComponents.SKILL_EXPERIENCE, crystalChestPlateItem.getOrDefault(DataComponents.SKILL_EXPERIENCE, 0));

        increaseLevelCap(stack, registryAccess, totalPoints);

        return stack;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRegistration.CRYSTAL_ELYTRA_RECIPE.get();
    }

    private List<ItemStack> getItems(CraftingInput container) {
        ItemStack elytraItem = ItemStack.EMPTY;
        ItemStack crystalChestPlateItem = ItemStack.EMPTY;

        for (int i = 0; i < container.size(); i++) {
            ItemStack itemStack = container.getItem(i);

            if (itemStack.is(ModRegistration.CRYSTAL_CHESTPLATE.get())) {
                crystalChestPlateItem = itemStack;
            } else if (itemStack.is(Items.ELYTRA)) {
                elytraItem = itemStack;
            }
        }

        return List.of(elytraItem, crystalChestPlateItem);
    }

    @Override
    public List<ItemStack> getInputs() {
        return List.of(new ItemStack(ModRegistration.CRYSTAL_CHESTPLATE.get()), new ItemStack(Items.ELYTRA));
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(ModRegistration.CRYSTAL_ELYTRA.get());
    }
}
