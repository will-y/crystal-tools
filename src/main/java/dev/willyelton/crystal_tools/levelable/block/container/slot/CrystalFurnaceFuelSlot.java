package dev.willyelton.crystal_tools.levelable.block.container.slot;

import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrystalFurnaceFuelSlot extends Slot {
    private final CrystalFurnaceContainer crystalFurnaceContainer;

    public CrystalFurnaceFuelSlot(CrystalFurnaceContainer crystalFurnaceContainer, int pSlot, int pX, int pY) {
        super(crystalFurnaceContainer.getBlockEntity(), pSlot, pX, pY);
        this.crystalFurnaceContainer = crystalFurnaceContainer;
    }

    public boolean mayPlace(@NotNull ItemStack pStack) {
        return this.crystalFurnaceContainer.getBlockEntity().isFuel(pStack);
    }

    @Override
    public boolean isActive() {
        return ArrayUtils.arrayContains(crystalFurnaceContainer.getActiveFuelSlots(), this.index);
    }
}
