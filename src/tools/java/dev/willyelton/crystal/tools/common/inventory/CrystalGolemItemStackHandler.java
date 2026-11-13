package dev.willyelton.crystal.tools.common.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class CrystalGolemItemStackHandler extends ItemStacksResourceHandler {
    private final int golemMaxStackSize;

    public CrystalGolemItemStackHandler(int size, int golemMaxStackSize) {
        super(size);
        this.golemMaxStackSize = golemMaxStackSize;
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return resource.isEmpty() ? golemMaxStackSize : Math.min(resource.getMaxStackSize(), golemMaxStackSize);
    }

    public ItemStack firstItem() {
        return this.stacks.stream().filter(s -> !s.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
    }

    public boolean isEmpty() {
        return this.stacks.stream().allMatch(ItemStack::isEmpty);
    }

    public boolean isFull() {
        return this.stacks.stream().noneMatch(ItemStack::isEmpty);
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        // TODO: Verify works and increase with skills
        return 16;
    }
}
