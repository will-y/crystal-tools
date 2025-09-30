package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.function.Predicate;

public class ItemStackUtils {
    public static ItemStack getHeldLevelableTool(Player player) {
        for (ItemStack i : player.getHandSlots()) {
            if (i.getItem() instanceof LevelableItem) {
                return i;
            }
        }

        return ItemStack.EMPTY;
    }

    public static boolean sameItem(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack1.is(itemStack2.getItem());
    }

    public static String toString(ItemStack stack) {
        if (stack.isEmpty()) {
            return "Empty Stack";
        }

        return String.format("%d x %s", stack.getCount(), stack.getItem());
    }

    public static ItemStack nextFuelItem(IItemHandlerModifiable handler, Predicate<ItemStack> predicate) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);

            if (!stack.isEmpty() && predicate.test(stack)) {
                ItemStack toReturn = stack.copyWithCount(1);
                ItemStack leftOver = stack.copy();
                leftOver.shrink(1);
                handler.setStackInSlot(i, leftOver);

                return toReturn;
            }
        }

        return ItemStack.EMPTY.copy();
    }
}
