package dev.willyelton.crystal_tools.common.inventory.container.slot;

import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class CrystalSlotItemHandler extends SlotItemHandler {
    private boolean active = true;

    public CrystalSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    // Not sure why this is commented out in Forge
    @Override
    public boolean isSameInventory(Slot other) {
        return other instanceof CrystalSlotItemHandler && ((CrystalSlotItemHandler) other).getItemHandler() == this.getItemHandler();
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
