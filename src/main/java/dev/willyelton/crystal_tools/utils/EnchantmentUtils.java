package dev.willyelton.crystal_tools.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EnchantmentUtils {
    /**
     * Adds the given enchantment to the item, removing all lower levels of that enchantment
     * @param stack - ItemStack to add the enchantment to
     * @param enchantment - The enchantment to add
     * @param level - The enchantment duration to add
     */
    public static void addEnchantment(ItemStack stack, ResourceKey<Enchantment> enchantment, int level, Player player) {
        HolderLookup.RegistryLookup<Enchantment> lookup = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(itemEnchantments);
        mutable.set(lookup.getOrThrow(enchantment), level);

        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
    }

    public static void removeEnchantment(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(itemEnchantments);

        mutable.removeIf(enchantmentHolder -> enchantmentMatches(enchantmentHolder, enchantment));

        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
    }

    public static boolean hasEnchantment(ItemStack stack, ResourceKey<Enchantment> enchantment) {
        return EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet().stream().anyMatch(enchantmentHolder -> enchantmentMatches(enchantmentHolder, enchantment));
    }

    private static boolean enchantmentMatches(Holder<Enchantment> holder, ResourceKey<Enchantment> enchantment) {
        return holder.unwrapKey().isPresent() && holder.unwrapKey().get().equals(enchantment);
    }

    /**
     * Gets the points that should be awarded from crafting with an enchanted item
     * @param stack The stack that is being used
     * @return The total number of enchantment levels
     */
    public static int pointsFromEnchantments(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()
                .stream()
                .mapToInt(Object2IntMap.Entry::getIntValue)
                .sum();
    }
}
