package dev.willyelton.crystal_tools.common.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

@Deprecated
public class ItemResourceHandlerAdapterModifiable implements IItemHandlerModifiable {
    private final ResourceHandler<ItemResource> handler;

    public static IItemHandlerModifiable of(ResourceHandler<ItemResource> handler) {
        return new ItemResourceHandlerAdapterModifiable(handler);
    }

    ItemResourceHandlerAdapterModifiable(ResourceHandler<ItemResource> handler) {
        this.handler = handler;
    }

    @Override
    public int getSlots() {
        return handler.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemUtil.getStack(handler, slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return ItemUtil.insertItemReturnRemaining(handler, slot, stack, simulate, null);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        var resource = handler.getResource(slot);
        if (resource.isEmpty()) {
            return ItemStack.EMPTY;
        }
        // We have to limit to the max stack size, per the contract of extractItem
        amount = Math.min(amount, resource.getMaxStackSize());
        try (var tx = Transaction.open(null)) {
            int extracted = handler.extract(slot, resource, amount, tx);
            if (!simulate) {
                tx.commit();
            }
            return resource.toStack(extracted);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getCapacityAsInt(slot, ItemResource.EMPTY);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return handler.isValid(slot, ItemResource.of(stack));
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        try (Transaction tx = Transaction.open(null)) {
            ItemResource resource = handler.getResource(slot);
            if (!resource.isEmpty()) {
                handler.extract(slot, resource, 99, tx);
            }

            if (!stack.isEmpty()) {
                handler.insert(ItemResource.of(stack), slot, tx);
            }

            tx.commit();
        }
    }
}
