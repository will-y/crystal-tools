package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
    public boolean matches(CraftingInput container, @NotNull Level level) {
        if (CrystalToolsConfig.DISABLE_AIOT.get()) return false;

        if (container.size() < 6) return false;

        Boolean[] itemsFound = new Boolean[requiredItems.length];
        Arrays.fill(itemsFound, false);

        for (int i = 0; i < container.size(); i++) {
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
    public @NotNull ItemStack assemble(CraftingInput container, HolderLookup.Provider registryAccess) {
        ItemStack result = new ItemStack(Registration.CRYSTAL_AIOT.get());

        List<ItemStack> levelableItems = this.getLevelableItems(container);

        int totalPoints = 0;

        for (ItemStack stack : levelableItems) {
            totalPoints += getPoints(stack);
        }

        result.set(DataComponents.SKILL_POINTS, totalPoints);

        increaseLevelCap(result, registryAccess, totalPoints);

        return result;
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return Registration.CRYSTAL_AIOT_RECIPE.get();
    }

    private List<ItemStack> getLevelableItems(CraftingInput container) {
        List<ItemStack> result = new ArrayList<>();

        for (int i = 0; i < container.size(); i++) {
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
