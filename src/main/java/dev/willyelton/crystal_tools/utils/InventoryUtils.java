package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.common.compat.curios.CuriosCompatibility;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.function.Predicate;

public class InventoryUtils {
    public static boolean removeItemFromInventory(Inventory inv, ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;

        int index = inv.findSlotMatchingItem(itemStack);

        if (index == -1) return false;

        inv.removeItem(index, 1);
        return true;
    }

    public static void copyTo(IItemHandlerModifiable source, IItemHandlerModifiable destination) {
        int maxIndex = Math.min(source.getSlots(), destination.getSlots());
        for (int i = 0; i < maxIndex; i++) {
            destination.setStackInSlot(i, source.getStackInSlot(i));
        }
    }

    public static void clear(IItemHandlerModifiable inv) {
        for (int i = 0; i < inv.getSlots(); i++) {
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public static boolean contains(IItemHandlerModifiable inv, ItemStack itemStack) {
        for (int i = 0; i < inv.getSlots(); i++) {
            if (ItemStack.isSameItemSameComponents(inv.getStackInSlot(i), itemStack)) return true;
        }

        return false;
    }

    public static ItemStack findItem(Inventory inv, Predicate<ItemStack> predicate) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (predicate.test(stack)) return stack;
        }

        return ItemStack.EMPTY;
    }

    public static List<ItemStack> findAll(Player player, Predicate<ItemStack> predicate) {
        List<ItemStack> curiosItems = CuriosCompatibility.getItemInCurios(player, predicate);
        List<ItemStack> playerInventoryItems = player.getInventory().getNonEquipmentItems()
                .stream()
                .filter(predicate)
                .toList();

        curiosItems.addAll(playerInventoryItems);

        return curiosItems;
    }

    public static List<ItemStack> getArmorItems(Player player) {
        return List.of(
                player.getItemBySlot(EquipmentSlot.HEAD),
                player.getItemBySlot(EquipmentSlot.CHEST),
                player.getItemBySlot(EquipmentSlot.LEGS),
                player.getItemBySlot(EquipmentSlot.FEET)
        );
    }

    public static List<ItemStack> getHandItems(Player player) {
        return List.of(
                player.getItemBySlot(EquipmentSlot.MAINHAND),
                player.getItemBySlot(EquipmentSlot.OFFHAND)
        );
    }
}
