package dev.willyelton.crystal_tools.common.inventory.container.slot.backpack;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class BackpackFilterSlot extends SubScreenSlot {
    public BackpackFilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    @Override
    public void onClicked(ItemStack stack) {
        ItemStack toInsert;
        if (stack.isEmpty()) {
            toInsert = ItemStack.EMPTY;
        } else {
            toInsert = stack.copy();
            toInsert.setCount(1);
        }

        set(toInsert);
        setChanged();
    }
}
