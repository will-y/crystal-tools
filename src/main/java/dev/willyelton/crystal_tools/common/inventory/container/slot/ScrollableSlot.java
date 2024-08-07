package dev.willyelton.crystal_tools.common.inventory.container.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerCopySlot;
import org.jetbrains.annotations.NotNull;

// TODO: Look at ScrollPanel, maybe we don't need all of this
public class ScrollableSlot extends ItemHandlerCopySlot {
    private int actualSlotIndex;
    private boolean active = true;

    public ScrollableSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.actualSlotIndex = index;
    }

    public void setSlotIndex(int slot) {
        this.actualSlotIndex = slot;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty())
            return false;
        return getItemHandler().isItemValid(actualSlotIndex, stack);
    }

    @Override
    public ItemStack getStackCopy() {
        return this.getItemHandler().getStackInSlot(actualSlotIndex);
    }

    // TODO: This is getting called when scrolling? Probably shouldn't be
    @Override
    public void setStackCopy(@NotNull ItemStack stack) {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(actualSlotIndex, stack);
    }

    @Override
    public int getMaxStackSize() {
        return getItemHandler().getSlotLimit(this.actualSlotIndex);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);

        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(actualSlotIndex);
        if (handler instanceof IItemHandlerModifiable handlerModifiable) {
            handlerModifiable.setStackInSlot(actualSlotIndex, ItemStack.EMPTY);

            ItemStack remainder = handlerModifiable.insertItem(actualSlotIndex, maxAdd, true);

            handlerModifiable.setStackInSlot(actualSlotIndex, currentStack);

            return maxInput - remainder.getCount();
        } else {
            ItemStack remainder = handler.insertItem(actualSlotIndex, maxAdd, true);

            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;
        }
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !this.getItemHandler().extractItem(actualSlotIndex, 1, true).isEmpty();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return other instanceof ItemHandlerCopySlot itemHandlerCopySlot && itemHandlerCopySlot.getItemHandler() == this.getItemHandler();
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
