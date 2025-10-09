package dev.willyelton.crystal_tools.common.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemAccessItemHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Version of {@link ItemAccessItemHandler} that can take more than 256 stacks
 */
public class ListComponentItemHandler extends ItemAccessResourceHandler<ItemResource> {
    private static final int MAX_CONTENTS_SIZE = 256;

    protected final Item validItem;
    protected final DataComponentType<List<ItemContainerContents>> component;

    public ListComponentItemHandler(ItemAccess itemAccess, DataComponentType<List<ItemContainerContents>> component, int size) {
        super(itemAccess, size);

        this.validItem = itemAccess.getResource().getItem();
        this.component = component;
    }

    protected List<ItemContainerContents> getContents(ItemResource accessResource) {
        return accessResource.getOrDefault(component, List.of());
    }

    protected ItemStack getStackFromContents(List<ItemContainerContents> contentsList, int slot) {
        int listIndex = slot / MAX_CONTENTS_SIZE;

        if (listIndex >= contentsList.size()) {
            return ItemStack.EMPTY;
        }

        ItemContainerContents contents = contentsList.get(listIndex);
        int contextIndex = listIndex % MAX_CONTENTS_SIZE;

        return contextIndex < contents.getSlots() ? contents.getStackInSlot(contextIndex) : ItemStack.EMPTY;
    }

    @Override
    protected ItemResource getResourceFrom(ItemResource accessResource, int index) {
        if (accessResource.is(validItem)) {
            return ItemResource.of(getStackFromContents(getContents(accessResource), index));
        } else {
            return ItemResource.EMPTY;
        }
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        if (accessResource.is(validItem)) {
            return getStackFromContents(getContents(accessResource), index).getCount();
        } else {
            return 0;
        }
    }

    @Override
    protected ItemResource update(ItemResource accessResource, int index, ItemResource newResource, int newAmount) {
        List<ItemContainerContents> contents = getContents(accessResource);

        int listIndex = index / MAX_CONTENTS_SIZE;
        int thisContentsIndex = listIndex % MAX_CONTENTS_SIZE;

        ItemContainerContents thisContent = contents.get(index);

        int thisContentsSlots = Math.clamp((this.size - listIndex * MAX_CONTENTS_SIZE), 0, MAX_CONTENTS_SIZE);
        NonNullList<ItemStack> list = NonNullList.withSize(Math.max(thisContent.getSlots(), thisContentsSlots), ItemStack.EMPTY);
        thisContent.copyInto(list);
        list.set(thisContentsIndex, newResource.toStack(newAmount));
        ItemContainerContents newContents = ItemContainerContents.fromItems(list);

        List<ItemContainerContents> copiedContents = deepCopy(contents);
        if (copiedContents.size() > index) {
            copiedContents.set(index, newContents);
        } else {
            copiedContents.add(newContents);
        }

        return accessResource.with(this.component, copiedContents);
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        // Any resource is valid, but we have to check that the item of the item access has not changed.
        return itemAccess.getResource().is(validItem);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return resource.isEmpty() ? Item.ABSOLUTE_MAX_STACK_SIZE : Math.min(resource.getMaxStackSize(), Item.ABSOLUTE_MAX_STACK_SIZE);
    }

    public void set(ItemStack stack, int index, ItemResource resource, int amount) {
        List<ItemContainerContents> result = update(ItemResource.of(stack), index, resource, amount).get(this.component);

        stack.set(this.component, result);

    }

    private List<ItemContainerContents> deepCopy(List<ItemContainerContents> containerContents) {
        List<ItemContainerContents> newContainerContents = new ArrayList<>();

        for (ItemContainerContents contents : containerContents) {
            NonNullList<ItemStack> newStacks = NonNullList.withSize(contents.getSlots(), ItemStack.EMPTY);
            contents.copyInto(newStacks);
            newContainerContents.add(ItemContainerContents.fromItems(newStacks));
        }

        return newContainerContents;
    }
}
