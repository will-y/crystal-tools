package dev.willyelton.crystal_tools.levelable.block.container.slot;

import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceFuelSlot extends Slot {
    private final CrystalFurnaceBlockEntity crystalFurnaceBlockEntity;

    public CrystalFurnaceFuelSlot(CrystalFurnaceBlockEntity crystalFurnaceBlockEntity, int pSlot, int pX, int pY) {
        super(crystalFurnaceBlockEntity, pSlot, pX, pY);
        this.crystalFurnaceBlockEntity = crystalFurnaceBlockEntity;
    }

    public boolean mayPlace(@NotNull ItemStack pStack) {
        return this.crystalFurnaceBlockEntity.isFuel(pStack);
    }

    @Override
    public boolean isActive() {
        return ArrayUtils.arrayContains(crystalFurnaceBlockEntity.getActiveFuelSlots(), this.index);
    }
}
