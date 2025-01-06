package dev.willyelton.crystal_tools.utils;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

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
}
