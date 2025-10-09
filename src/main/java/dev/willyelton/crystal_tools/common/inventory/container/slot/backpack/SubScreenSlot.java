package dev.willyelton.crystal_tools.common.inventory.container.slot.backpack;

import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public abstract class SubScreenSlot extends CrystalSlotItemHandler {
    public SubScreenSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> indexModifier, int index, int xPosition, int yPosition) {
        super(handler, indexModifier, index, xPosition, yPosition);
    }

    public abstract void onClicked(ItemStack stack);
}
