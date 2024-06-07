package dev.willyelton.crystal_tools.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EnchantmentUtils {
    /**
     * Adds the given enchantment to the item, removing all lower levels of that enchantment
     * @param stack - ItemStack to add the enchantment to
     * @param enchantment - The enchantment to add
     * @param level - The enchantment level to add
     */
    public static void addEnchantment(ItemStack stack, Enchantment enchantment, int level) {
        ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(itemEnchantments);
        mutable.set(enchantment, level);

        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
    }

    public static void removeEnchantment(ItemStack stack, Enchantment enchantment) {
        ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(itemEnchantments);

        mutable.removeIf(enchantmentHolder -> enchantmentHolder.value().equals(enchantment));

        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
    }

    public static boolean hasEnchantment(ItemStack stack, Enchantment enchantment) {
        return EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet().stream().anyMatch(enchantmentHolder -> enchantmentHolder.value().equals(enchantment));
    }
}
