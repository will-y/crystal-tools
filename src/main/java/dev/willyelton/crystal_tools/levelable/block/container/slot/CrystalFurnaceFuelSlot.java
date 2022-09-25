package dev.willyelton.crystal_tools.levelable.block.container.slot;

import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceFuelSlot extends Slot {
    private final CrystalFurnaceBlockEntity container;

    public CrystalFurnaceFuelSlot(CrystalFurnaceBlockEntity container, int pSlot, int pX, int pY) {
        super(container, pSlot, pX, pY);
        this.container = container;
    }

    public boolean mayPlace(@NotNull ItemStack pStack) {
        return this.container.isFuel(pStack);
    }
}
