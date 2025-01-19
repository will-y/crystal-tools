package dev.willyelton.crystal_tools.common.levelable.block.entity.action;

import net.minecraft.world.item.ItemStack;

import java.util.Map;

public interface AutoOutputable {
    /**
     * Gets the itemstacks that should be auto outputted to other inventories
     * @return A map of the stack's index to the itemstack that is stored in that index
     */
    Map<Integer, ItemStack> getOutputStacks();

    /**
     * Sets the item in the inventory.
     * Used to set the item if there is a remainder after inserting into adjacent inventories.
     * @param slot The slot index to insert into
     * @param stack The stack to insert
     */
    void setItem(int slot, ItemStack stack);
}
