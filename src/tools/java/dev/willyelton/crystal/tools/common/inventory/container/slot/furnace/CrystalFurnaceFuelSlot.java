package dev.willyelton.crystal.tools.common.inventory.container.slot.furnace;

import dev.willyelton.crystal.tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal.core.common.inventory.container.slot.CrystalSlotItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class CrystalFurnaceFuelSlot extends CrystalSlotItemHandler {
    private final CrystalFurnaceContainerMenu crystalFurnaceContainerMenu;

    public CrystalFurnaceFuelSlot(CrystalFurnaceContainerMenu crystalFurnaceContainerMenu, ItemStacksResourceHandler itemHandler, int index, int x, int y) {
        super(itemHandler, index, x, y);
        this.crystalFurnaceContainerMenu = crystalFurnaceContainerMenu;
    }

    public boolean mayPlace(ItemStack stack) {
        return this.crystalFurnaceContainerMenu.getBlockEntity().isFuel(stack);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && crystalFurnaceContainerMenu.getNumActiveFuelSlots() > this.getIndex();
    }
}
