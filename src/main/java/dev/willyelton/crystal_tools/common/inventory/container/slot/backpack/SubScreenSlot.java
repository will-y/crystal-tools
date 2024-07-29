package dev.willyelton.crystal_tools.common.inventory.container.slot.backpack;

import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class SubScreenSlot extends CrystalSlotItemHandler {
    public SubScreenSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public abstract void onClicked(ItemStack stack);
}
