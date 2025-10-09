package dev.willyelton.crystal_tools.utils;

import com.google.common.base.Predicates;
import dev.willyelton.crystal_tools.common.compat.curios.CuriosCompatibility;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
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

    public static List<ItemStack> tryInsertStacks(ResourceHandler<ItemResource> handler, List<ItemStack> stacksToInsert) {
        return tryInsertStacks(handler, stacksToInsert, Predicates.alwaysTrue());
    }

    /**
     * Tries to insert items into an item handler. Any that don't fit are returned
     * @param handler The handler to insert into
     * @param stacksToInsert The itemstacks to insert
     * @param filter A predicate that decides if a given item should be inserted
     * @return The items that didn't fit in the handler
     */
    public static List<ItemStack> tryInsertStacks(ResourceHandler<ItemResource> handler, List<ItemStack> stacksToInsert, Predicate<ItemStack> filter) {
        List<ItemStack> noFit = new ArrayList<>();

        for (ItemStack stack : stacksToInsert) {
            if (!filter.test(stack)) {
                continue;
            }
            try (Transaction tx = Transaction.open(null)) {
                ItemStack result = ItemUtil.insertItemReturnRemaining(handler, stack, false, tx);
                if (!result.isEmpty()) {
                    noFit.add(result);
                }

                tx.commit();
            }
        }

        return noFit;
    }
}
