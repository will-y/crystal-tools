package dev.willyelton.crystal_tools.common.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.function.BiConsumer;

public class CallbackItemStackHandler extends ItemStacksResourceHandler {
    // New item, old item
    private final BiConsumer<ItemStack, ItemStack> callback;

    public CallbackItemStackHandler(NonNullList<ItemStack> stacks, BiConsumer<ItemStack, ItemStack> callback) {
        super(stacks);
        this.callback = callback;
    }

    @Override
    protected void onContentsChanged(int index, ItemStack previousContents) {
        callback.accept(previousContents, stacks.get(index));
    }
}
