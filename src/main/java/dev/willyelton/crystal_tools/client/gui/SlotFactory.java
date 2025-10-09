package dev.willyelton.crystal_tools.client.gui;

import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

@FunctionalInterface
public interface SlotFactory<T extends Slot> {
    T create(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> indexModifier, int index, int x, int y);
}
