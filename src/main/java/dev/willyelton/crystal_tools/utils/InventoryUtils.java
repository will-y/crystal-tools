package dev.willyelton.crystal_tools.utils;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InventoryUtils {
    public static boolean removeItemFromInventory(Inventory inv, ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;

        int index = inv.findSlotMatchingItem(itemStack);

        if (index == -1) return false;

        inv.removeItem(index, 1);
        return true;
    }

    public static void copyTo(ItemStackHandler source, ItemStackHandler destination) {
        int maxIndex = Math.min(source.getSlots(), destination.getSlots());
        for (int i = 0; i < maxIndex; i++) {
            destination.setStackInSlot(i, source.getStackInSlot(i));
        }
    }

    public static void clear(ItemStackHandler inv) {
        for (int i = 0; i < inv.getSlots(); i++) {
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public static boolean contains(ItemStackHandler inv, ItemStack itemStack) {
        for (int i = 0; i < inv.getSlots(); i++) {
            if (inv.getStackInSlot(i).equals(itemStack, false)) return true;
        }

        return false;
    }
}
