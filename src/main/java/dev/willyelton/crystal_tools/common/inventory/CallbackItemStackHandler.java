package dev.willyelton.crystal_tools.common.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.function.BiConsumer;

public class CallbackItemStackHandler extends ItemStackHandler {
    // New item, old item
    private final BiConsumer<ItemStack, ItemStack> callback;

    public CallbackItemStackHandler(NonNullList<ItemStack> stacks, BiConsumer<ItemStack, ItemStack> callback) {
        super(stacks);
        this.callback = callback;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        ItemStack originalStack = getStackInSlot(slot);
        super.setStackInSlot(slot, stack);
        callback.accept(stack, originalStack);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        ItemStack originalStack = getStackInSlot(slot);
        ItemStack result = super.insertItem(slot, stack, simulate);

        if (!simulate) {
            callback.accept(stack, originalStack);
        }

        return result;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack result = super.extractItem(slot, amount, simulate);

        if (!simulate) {
            callback.accept(getStackInSlot(slot), result);
        }
        return result;
    }
}
