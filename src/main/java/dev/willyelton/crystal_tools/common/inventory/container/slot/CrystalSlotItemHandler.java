package dev.willyelton.crystal_tools.common.inventory.container.slot;

import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class CrystalSlotItemHandler extends ResourceHandlerSlot {
    private boolean active = true;
    private final int index;

    public CrystalSlotItemHandler(ItemStacksResourceHandler itemHandler, int index, int xPosition, int yPosition) {
        this(itemHandler, itemHandler::set, index, xPosition, yPosition);
    }

    public CrystalSlotItemHandler(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition) {
        super(itemHandler, slotModifier, index, xPosition, yPosition);
        this.index = index;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getIndex() {
        return index;
    }
}