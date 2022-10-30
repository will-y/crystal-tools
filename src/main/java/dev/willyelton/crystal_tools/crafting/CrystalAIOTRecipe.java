package dev.willyelton.crystal_tools.crafting;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.levelable.LevelableItem;
import dev.willyelton.crystal_tools.levelable.tool.ModTools;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrystalAIOTRecipe extends CustomRecipe {
    private static final Item[] requiredItems = new Item[] {
            ModTools.CRYSTAL_AXE.get(),
            ModTools.CRYSTAL_PICKAXE.get(),
            ModTools.CRYSTAL_SHOVEL.get(),
            ModTools.CRYSTAL_HOE.get(),
            ModTools.CRYSTAL_SWORD.get(),
            Items.SLIME_BLOCK
    };

    public CrystalAIOTRecipe(ResourceLocation location) {
        super(location);
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
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container) {
        ItemStack result = new ItemStack(ModTools.CRYSTAL_AIOT.get());

        List<ItemStack> levelableItems = this.getLevelableItems(container);

        int totalPoints = 0;

        for (ItemStack stack : levelableItems) {
            int[] points = NBTUtils.getIntArray(stack, "points");
            totalPoints += Arrays.stream(points).sum() + (int) NBTUtils.getFloatOrAddKey(stack, "skill_points");
        }

        NBTUtils.setValue(result, "skill_points", totalPoints);

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
}
