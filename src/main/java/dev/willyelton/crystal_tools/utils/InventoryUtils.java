package dev.willyelton.crystal_tools.utils;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

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
}
