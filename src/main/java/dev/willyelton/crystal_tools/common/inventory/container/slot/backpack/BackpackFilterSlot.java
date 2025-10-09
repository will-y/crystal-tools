package dev.willyelton.crystal_tools.common.inventory.container.slot.backpack;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class BackpackFilterSlot extends SubScreenSlot {
    public BackpackFilterSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> indexModifier, int index, int xPosition, int yPosition) {
        super(handler, indexModifier, index, xPosition, yPosition);
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
