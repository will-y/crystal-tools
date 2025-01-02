package dev.willyelton.crystal_tools.client.gui;

import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.items.IItemHandler;

@FunctionalInterface
public interface SlotFactory<T extends Slot> {
    T create(IItemHandler handler, int index, int x, int y);
}
