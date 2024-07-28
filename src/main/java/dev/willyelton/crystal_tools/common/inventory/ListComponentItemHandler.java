package dev.willyelton.crystal_tools.common.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * Version of {@link ComponentItemHandler} that can take more than 256 stacks
 */
public class ListComponentItemHandler implements IItemHandlerModifiable {
    private static final int MAX_CONTENTS_SIZE = 256;
    protected final MutableDataComponentHolder parent;
    protected final DataComponentType<List<ItemContainerContents>> component;
    protected final int size;

    public ListComponentItemHandler(MutableDataComponentHolder parent, DataComponentType<List<ItemContainerContents>> component, int size) {
        this.parent = parent;
        this.component = component;
        this.size = size;
    }

    @Override
    public int getSlots() {
        return this.size;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        ItemContainerContents contents = this.getContents(slot);
        return this.getStackFromContents(contents, slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        if (!this.isItemValid(slot, stack)) {
            throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
        }
        ItemContainerContents contents = this.getContents(slot);
        ItemStack existing = this.getStackFromContents(contents, slot);
        if (!ItemStack.matches(stack, existing)) {
            this.updateContents(contents, stack, slot);
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack toInsert, boolean simulate) {
        this.validateSlotIndex(slot);

        if (toInsert.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!this.isItemValid(slot, toInsert)) {
            return toInsert;
        }

        ItemContainerContents contents = this.getContents(slot);
        ItemStack existing = this.getStackFromContents(contents, slot);
        // Max amount of the stack that could be inserted
        int insertLimit = Math.min(this.getSlotLimit(slot), toInsert.getMaxStackSize());

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(toInsert, existing)) {
                return toInsert;
            }

            insertLimit -= existing.getCount();
        }

        if (insertLimit <= 0) {
            return toInsert;
        }

        int inserted = Math.min(insertLimit, toInsert.getCount());

        if (!simulate) {
            this.updateContents(contents, toInsert.copyWithCount(existing.getCount() + inserted), slot);
        }

        return toInsert.copyWithCount(toInsert.getCount() - inserted);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        this.validateSlotIndex(slot);

        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        ItemContainerContents contents = this.getContents(slot);
        ItemStack existing = this.getStackFromContents(contents, slot);

        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (!simulate) {
            this.updateContents(contents, existing.copyWithCount(existing.getCount() - toExtract), slot);
        }

        return existing.copyWithCount(toExtract);
    }

    @Override
    public int getSlotLimit(int slot) {
        return Item.ABSOLUTE_MAX_STACK_SIZE;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getItem().canFitInsideContainerItems();
    }

    protected ItemContainerContents getContents(int slot) {
        int index = slot / MAX_CONTENTS_SIZE;
        List<ItemContainerContents> contentsList = this.parent.getOrDefault(this.component, new ArrayList<>());

        if (contentsList.size() <= index) {
            return ItemContainerContents.EMPTY;
        }

        return contentsList.get(index);
    }

    protected ItemStack getStackFromContents(ItemContainerContents contents, int slot) {
        this.validateSlotIndex(slot);
        int contentsIndex = slot % MAX_CONTENTS_SIZE;
        return contents.getSlots() <= contentsIndex ? ItemStack.EMPTY : contents.getStackInSlot(contentsIndex);
    }

    protected void updateContents(ItemContainerContents contents, ItemStack stack, int slot) {
        this.validateSlotIndex(slot);
        int index = slot / MAX_CONTENTS_SIZE;
        int thisContentsSlots = (this.size - index * MAX_CONTENTS_SIZE) % MAX_CONTENTS_SIZE;
        NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlots(), thisContentsSlots), ItemStack.EMPTY);
        contents.copyInto(list);
        list.set(slot, stack);
        ItemContainerContents newContents = ItemContainerContents.fromItems(list);
        List<ItemContainerContents> oldContents = this.parent.getOrDefault(this.component, new ArrayList<>());
        // TODO: We might have to copy all of the components?
        if (oldContents.size() > index) {
            oldContents.set(index, newContents);
        } else {
            oldContents.add(newContents);
        }

        this.parent.set(this.component, oldContents);
    }

    protected final void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getSlots()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
        }
    }
}
