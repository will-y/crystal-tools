package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CrystalElytraRecipe extends CrystalToolsRecipe {

    public CrystalElytraRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        if (CrystalToolsConfig.DISABLE_ELYTRA.get()) return false;

        boolean foundElytra = false;
        boolean foundChestplate = false;

        for (int i = 0; i < container.size(); i++) {
            ItemStack itemStack = container.getItem(i);

            if (itemStack.is(Registration.CRYSTAL_CHESTPLATE.get())) {
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
    public @NotNull ItemStack assemble(CraftingInput container, HolderLookup.Provider registryAccess) {
        ItemStack stack = new ItemStack(Registration.CRYSTAL_ELYTRA.get());
        List<ItemStack> items = this.getItems(container);

        ItemStack elytraItem = items.get(0);
        ItemStack crystalChestPlateItem = items.get(1);

        if (crystalChestPlateItem.isEmpty()) {
            return crystalChestPlateItem;
        }

        List<Integer> points = crystalChestPlateItem.getOrDefault(DataComponents.POINTS_ARRAY, Collections.emptyList());

        // points in chestplate
        int spentPoints = points.stream().mapToInt(Integer::intValue).sum();
        // unspent points in chestplate
        int unspentPoints = crystalChestPlateItem.getOrDefault(DataComponents.SKILL_POINTS, 0);

        // points from enchantments on elytra
        int enchantmentPoints = EnchantmentHelper.getEnchantmentsForCrafting(elytraItem).entrySet().stream().mapToInt(Object2IntMap.Entry::getIntValue).sum();

        int totalPoints = spentPoints + unspentPoints + enchantmentPoints;

        // Set skill points
        stack.set(DataComponents.SKILL_POINTS, totalPoints);

        // Set exp cap
        ToolUtils.increaseExpCap(stack, totalPoints);

        stack.set(DataComponents.SKILL_EXPERIENCE, crystalChestPlateItem.getOrDefault(DataComponents.SKILL_EXPERIENCE, 0));

        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Registration.CRYSTAL_ELYTRA_RECIPE.get();
    }

    private List<ItemStack> getItems(CraftingInput container) {
        ItemStack elytraItem = ItemStack.EMPTY;
        ItemStack crystalChestPlateItem = ItemStack.EMPTY;


        for (int i = 0; i < container.size(); i++) {
            ItemStack itemStack = container.getItem(i);

            if (itemStack.is(Registration.CRYSTAL_CHESTPLATE.get())) {
                crystalChestPlateItem = itemStack;
            } else if (itemStack.is(Items.ELYTRA)) {
                elytraItem = itemStack;
            }
        }

        return List.of(elytraItem, crystalChestPlateItem);
    }

    @Override
    public List<ItemStack> getInputs() {
        return List.of(new ItemStack(Registration.CRYSTAL_CHESTPLATE.get()), new ItemStack(Items.ELYTRA));
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Registration.CRYSTAL_ELYTRA.get());
    }
}
