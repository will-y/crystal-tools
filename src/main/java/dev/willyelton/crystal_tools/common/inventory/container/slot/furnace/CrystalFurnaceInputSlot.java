package dev.willyelton.crystal_tools.common.inventory.container.slot.furnace;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystalFurnaceInputSlot extends CrystalSlotItemHandler {
    private final CrystalFurnaceContainerMenu crystalFurnaceContainerMenu;
    private final TriConsumer<ItemStack, ItemStack, Integer> onSet;

    public CrystalFurnaceInputSlot(CrystalFurnaceContainerMenu crystalFurnaceContainerMenu, ItemStacksResourceHandler itemHandler, int index, int x, int y,
                                   @Nullable TriConsumer<ItemStack, ItemStack, Integer> onSet) {
        super(itemHandler, index, x, y);
        this.crystalFurnaceContainerMenu = crystalFurnaceContainerMenu;
        this.onSet = onSet == null ? (a, b, c) -> {} : onSet;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return crystalFurnaceContainerMenu.getBlockEntity().hasRecipe(stack);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && crystalFurnaceContainerMenu.getNumActiveSlots() > this.getIndex();
    }

    @Override
    protected void setStackCopy(ItemStack stack) {
        ItemStack previous = this.getItem();
        super.setStackCopy(stack);
        this.onSet.accept(stack, previous, this.getIndex());
    }
}
