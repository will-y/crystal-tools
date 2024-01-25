package dev.willyelton.crystal_tools.inventory;

import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class CrystalBackpackInventory extends ItemStackHandler {
    private boolean isDirty;
    private ItemStack stack;

    public CrystalBackpackInventory(int size) {
        super(size);
        this.stack = ItemStack.EMPTY;
    }

    public CrystalBackpackInventory(ItemStack stack) {
        super((int) NBTUtils.getFloatOrAddKey(stack, "rows", 1) * 9);
        this.stack = stack;
    }

    public boolean isDirty() {
        boolean state = isDirty;
        isDirty = false;
        return state;
    }
}
