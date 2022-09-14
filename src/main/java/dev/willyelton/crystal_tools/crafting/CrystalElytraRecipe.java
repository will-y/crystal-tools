package dev.willyelton.crystal_tools.crafting;

import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.item.armor.ModArmor;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CrystalElytraRecipe extends CustomRecipe {

    public CrystalElytraRecipe(ResourceLocation pId) {
        super(pId);
    }

    @Override
    public boolean matches(@NotNull CraftingContainer container, @NotNull Level level) {
        boolean foundElytra = false;
        boolean foundChestplate = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);

            if (itemStack.is(ModArmor.CRYSTAL_CHESTPLATE.get())) {
                if (foundChestplate)
                    return false;
                foundChestplate = true;
            } else if (itemStack.is(Items.ELYTRA)) {
                if (foundElytra)
                    return false;
                foundElytra = true;
            }
        }

        return foundChestplate && foundElytra;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container) {
        ItemStack stack = new ItemStack(ModArmor.CRYSTAL_ELYTRA.get());
        List<ItemStack> items = this.getItems(container);

        ItemStack elytraItem = items.get(0);
        ItemStack crystalChestPlateItem = items.get(1);

        if (crystalChestPlateItem.isEmpty()) {
            return crystalChestPlateItem;
        }

        int[] points = NBTUtils.getIntArray(crystalChestPlateItem, "points");

        // points in chestplate
        int spentPoints = Arrays.stream(points).sum();
        // unspent points in chestplate
        int unspentPoints = (int) NBTUtils.getFloatOrAddKey(crystalChestPlateItem, "skill_points");

        // points from enchantments on elytra
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(elytraItem);
        int enchantmentPoints = enchantments.values().stream().mapToInt(Integer::intValue).sum();

        int totalPoints = spentPoints + unspentPoints + enchantmentPoints;

        // Set skill points
        NBTUtils.setValue(stack, "skill_points", totalPoints);

        // Set exp cap
        ToolUtils.increaseExpCap(stack, totalPoints);

        // Set exp
        NBTUtils.setValue(stack, "experience", NBTUtils.getFloatOrAddKey(crystalChestPlateItem, "experience"));

        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.CRYSTAL_ELYTRA_RECIPE.get();
    }

    private List<ItemStack> getItems(CraftingContainer container) {
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

        return List.of(elytraItem, crystalChestPlateItem);
    }
}