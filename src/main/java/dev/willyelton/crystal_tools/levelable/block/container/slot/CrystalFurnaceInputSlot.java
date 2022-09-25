package dev.willyelton.crystal_tools.levelable.block.container.slot;

import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CrystalFurnaceInputSlot extends Slot {
    private final CrystalFurnaceBlockEntity crystalFurnaceBlockEntity;

    public CrystalFurnaceInputSlot(CrystalFurnaceBlockEntity crystalFurnaceBlockEntity, int pSlot, int pX, int pY) {
        super(crystalFurnaceBlockEntity, pSlot, pX, pY);
        this.crystalFurnaceBlockEntity = crystalFurnaceBlockEntity;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return crystalFurnaceBlockEntity.hasRecipe(stack);
    }

    @Override
    public boolean isActive() {
        return ArrayUtils.arrayContains(crystalFurnaceBlockEntity.getActiveInputSlots(), this.index);
    }
}
