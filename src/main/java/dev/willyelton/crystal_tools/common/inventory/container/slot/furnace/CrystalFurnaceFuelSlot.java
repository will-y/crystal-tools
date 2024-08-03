package dev.willyelton.crystal_tools.common.inventory.container.slot.furnace;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CrystalFurnaceFuelSlot extends Slot {
    private final CrystalFurnaceContainerMenu crystalFurnaceContainerMenu;

    public CrystalFurnaceFuelSlot(CrystalFurnaceContainerMenu crystalFurnaceContainerMenu, int pSlot, int pX, int pY) {
        super(crystalFurnaceContainerMenu.getBlockEntity(), pSlot, pX, pY);
        this.crystalFurnaceContainerMenu = crystalFurnaceContainerMenu;
    }

    public boolean mayPlace(ItemStack stack) {
        return this.crystalFurnaceContainerMenu.getBlockEntity().isFuel(stack);
    }

    @Override
    public boolean isActive() {
        return ArrayUtils.arrayContains(crystalFurnaceContainerMenu.getActiveFuelSlots(), this.index);
    }
}
