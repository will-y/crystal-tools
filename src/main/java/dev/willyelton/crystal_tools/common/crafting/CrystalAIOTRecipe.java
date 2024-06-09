package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.DataComponents;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CrystalAIOTRecipe extends CrystalToolsRecipe {
    private static final Item[] requiredItems = new Item[] {
            Registration.CRYSTAL_AXE.get(),
            Registration.CRYSTAL_PICKAXE.get(),
            Registration.CRYSTAL_SHOVEL.get(),
            Registration.CRYSTAL_HOE.get(),
            Registration.CRYSTAL_SWORD.get(),
            Items.SLIME_BLOCK
    };

    public CrystalAIOTRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
        if (CrystalToolsConfig.DISABLE_AIOT.get()) return false;

        Boolean[] itemsFound = new Boolean[requiredItems.length];
        Arrays.fill(itemsFound, false);

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            boolean foundItem = false;

            for (int j = 0; j < requiredItems.length; j++) {
                if (itemStack.is(requiredItems[j])) {
                    if (itemsFound[j]) {
                        return false;
                    }
                    itemsFound[j] = true;
                    foundItem = true;
                }
            }

            if (!foundItem && !itemStack.isEmpty()) {
                return false;
            }
        }

        return ArrayUtils.indexOf(itemsFound, false) == -1;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container, @NotNull HolderLookup.Provider registryAccess) {
        ItemStack result = new ItemStack(Registration.CRYSTAL_AIOT.get());

        List<ItemStack> levelableItems = this.getLevelableItems(container);

        int totalPoints = 0;

        for (ItemStack stack : levelableItems) {
            List<Integer> points = stack.getOrDefault(DataComponents.POINTS_ARRAY, Collections.emptyList());
            totalPoints += points.stream().mapToInt(Integer::intValue).sum() + stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
        }

        result.set(DataComponents.SKILL_POINTS, totalPoints);

        ToolUtils.increaseExpCap(result, totalPoints);

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 6;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.CRYSTAL_AIOT_RECIPE.get();
    }

    private List<ItemStack> getLevelableItems(CraftingContainer container) {
        List<ItemStack> result = new ArrayList<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.getItem() instanceof LevelableItem) {
                result.add(stack);
            }
        }

        return result;
    }

    @Override
    public List<ItemStack> getInputs() {
        return Arrays.stream(requiredItems).map(ItemStack::new).toList();
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Registration.CRYSTAL_AIOT.get());
    }
}
