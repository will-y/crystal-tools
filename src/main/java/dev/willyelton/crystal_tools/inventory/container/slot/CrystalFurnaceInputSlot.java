package dev.willyelton.crystal_tools.inventory.container.slot;

import dev.willyelton.crystal_tools.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceInputSlot extends Slot {
    private final CrystalFurnaceContainerMenu crystalFurnaceContainerMenu;

    public CrystalFurnaceInputSlot(CrystalFurnaceContainerMenu crystalFurnaceContainerMenu, int pSlot, int pX, int pY) {
        super(crystalFurnaceContainerMenu.getBlockEntity(), pSlot, pX, pY);
        this.crystalFurnaceContainerMenu = crystalFurnaceContainerMenu;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return crystalFurnaceContainerMenu.getBlockEntity().hasRecipe(stack);
    }

    @Override
    public boolean isActive() {
        return ArrayUtils.arrayContains(crystalFurnaceContainerMenu.getActiveInputSlots(), this.index);
    }
}
