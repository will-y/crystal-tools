package dev.willyelton.crystal_tools.inventory.container.slot;

import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CrystalSlotItemHandler extends SlotItemHandler {
    public CrystalSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    // Not sure why this is commented out in Forge
    @Override
    public boolean isSameInventory(Slot other) {
        return other instanceof SlotItemHandler && ((SlotItemHandler) other).getItemHandler() == this.getItemHandler();
    }
}
