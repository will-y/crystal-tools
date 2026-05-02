package dev.willyelton.crystal.core.common.block.entity.action;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AutoOutputable {
   List<Direction> POSSIBLE_DIRECTIONS = Arrays.stream(new Direction[] {Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}).toList();

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

    default Collection<Direction> possibleDirections() {
        return POSSIBLE_DIRECTIONS;
    }
}
