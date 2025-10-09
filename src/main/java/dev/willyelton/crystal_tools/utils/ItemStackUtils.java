package dev.willyelton.crystal_tools.utils;

import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.function.Predicate;

public class ItemStackUtils {
    public static ItemStack getHeldLevelableTool(Player player) {
        for (ItemStack i : InventoryUtils.getHandItems(player)) {
            Levelable levelable = i.getCapability(Capabilities.ITEM_SKILL, player.level().registryAccess());
            if (levelable != null) {
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

    public static void removeAllComponents(ItemStack stack) {
        stack.getComponentsPatch().entrySet().forEach(entry -> {
            stack.remove(entry.getKey());
        });
    }

    public static ItemStack nextFuelItem(ResourceHandler<ItemResource> handler, Predicate<ItemResource> predicate) {
        for (int i = 0; i < handler.size(); i++) {
            ItemResource resource = handler.getResource(i);

            if (!resource.isEmpty() && predicate.test(resource)) {
                try (Transaction tx = Transaction.open(null)) {
                    int amount = handler.extract(i, resource, 1, tx);
                    tx.commit();

                    if (amount > 0) {
                        return resource.toStack(amount);
                    }
                }
            }
        }

        return ItemStack.EMPTY.copy();
    }
}
