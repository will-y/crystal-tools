package dev.willyelton.crystal_tools.levelable.block.container.slot;

import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.ArrayUtils;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CrystalFurnaceInputSlot extends Slot {
    private final CrystalFurnaceContainer crystalFurnaceContainer;

    public CrystalFurnaceInputSlot(CrystalFurnaceContainer crystalFurnaceContainer, int pSlot, int pX, int pY) {
        super(crystalFurnaceContainer.getBlockEntity(), pSlot, pX, pY);
        this.crystalFurnaceContainer = crystalFurnaceContainer;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return crystalFurnaceContainer.getBlockEntity().hasRecipe(stack);
    }

    @Override
    public boolean isActive() {
        return ArrayUtils.arrayContains(crystalFurnaceContainer.getActiveInputSlots(), this.index);
    }
}
