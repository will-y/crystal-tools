package dev.willyelton.crystal_tools.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class EnchantmentUtils {
    /**
     * Adds the given enchantment to the item, removing all lower levels of that enchantment
     * @param stack - ItemStack to add the enchantment to
     * @param enchantment - The enchantment to add
     * @param level - The enchantment level to add
     */
    public static void addEnchantment(ItemStack stack, Enchantment enchantment, int level) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        enchantments.put(enchantment, level);

        EnchantmentHelper.setEnchantments(enchantments, stack);

//        for (Enchantment enchantmentIn : enchantments.keySet()) {
//            if (enchantmentIn.equals(enchantment)) {
//                enchantments.put(enchantment, level);
//            }
//        }
    }
}
