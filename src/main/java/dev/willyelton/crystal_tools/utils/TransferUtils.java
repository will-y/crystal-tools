package dev.willyelton.crystal_tools.utils;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * My utils for the transfer rework
 */
public class TransferUtils {
    public static IndexModifier<ItemResource> playerIndexModifier(Inventory inventory) {
        return (index, resource, amount) -> {
            inventory.setItem(index, resource.toStack(amount));
            inventory.setChanged();
        };
    }

    public static void clear(ResourceHandler<ItemResource> handler) {
        try (Transaction tx = Transaction.open(null)) {
            for (int i = 0; i < handler.size(); i++) {
                ItemResource resource = handler.getResource(i);
                if (!resource.isEmpty()) {
                    // TODO: if there is ever a long amount will need to put this in a while loop or something
                    handler.extract(i, resource, handler.getCapacityAsInt(i, resource), tx);
                }
            }

            tx.commit();
        }
    }

    // TODO: Doing this seems wrong and doesn't work for some reason
    public static IndexModifier<ItemResource> indexModifier(ResourceHandler<ItemResource> handler) {
        return (index, resource, amount) -> {
            try (Transaction tx = Transaction.open(null)) {
                ItemResource invResource = handler.getResource(index);
                if (!invResource.isEmpty()) {
                    handler.extract(index, invResource, handler.getCapacityAsInt(index, invResource), tx);
                }

                if (!resource.isEmpty()) {
                    handler.insert(index, resource, amount, tx);
                }

                tx.commit();
            }
        };
    }

    public static IndexModifier<ItemResource> itemstackIndexModifier(ItemStack stack) {
        return (index, resource, amount) -> {
            ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlots(), 256), ItemStack.EMPTY);
            contents.copyInto(list);
            list.set(index, resource.toStack(amount));
            stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(list));
        };
    }

    // TODO: Won't work if something stores more than a stack in a slot
    public static List<ItemStack> extractAll(ResourceHandler<ItemResource> handler) {
        List<ItemStack> items = new ArrayList<>();
        try (Transaction tx = Transaction.open(null)) {
            for (int i = 0; i < handler.size(); i++) {
                ItemResource resource = handler.getResource(i);
                if (!resource.isEmpty()) {
                    int extracted = handler.extract(i, resource, handler.getCapacityAsInt(i, resource), tx);
                    if (extracted > 0) {
                        items.add(resource.toStack(extracted));
                    }
                }
            }
            tx.commit();
        }

        return items;
    }
}
