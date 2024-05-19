package dev.willyelton.crystal_tools.inventory.container.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public class ScrollableSlot extends CrystalSlotItemHandler {
    private int actualSlotIndex;

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
    @NotNull
    public ItemStack getItem() {
        return this.getItemHandler().getStackInSlot(actualSlotIndex);
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    @Override
    public void set(@NotNull ItemStack stack) {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(actualSlotIndex, stack);
        this.setChanged();
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    // @Override
    public void initialize(ItemStack stack) {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(actualSlotIndex, stack);
        this.setChanged();
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
    @NotNull
    public ItemStack remove(int amount) {
        return this.getItemHandler().extractItem(actualSlotIndex, amount, false);
    }
}
