package dev.willyelton.crystal_tools.inventory;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CrystalBackpackInventory extends ItemStackHandler {
    private ItemStack backpackStack;

    public CrystalBackpackInventory(int size) {
        super(size);
        this.backpackStack = ItemStack.EMPTY;
    }

    public CrystalBackpackInventory(ItemStack stack) {
        super((int) NBTUtils.getFloatOrAddKey(stack, "capacity", 1) * 9);
        this.backpackStack = stack;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !stack.is(Registration.CRYSTAL_BACKPACK.get());
    }

    public ItemStack insertStack(ItemStack stack) {
        return ItemHandlerHelper.insertItem(this, stack, false);
    }
}
