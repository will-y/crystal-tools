package dev.willyelton.crystal_tools.common.inventory.container.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class ReadOnlySlot extends CrystalSlotItemHandler {
    public ReadOnlySlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> indexModifier, int index, int xPosition, int yPosition) {
        super(handler, indexModifier, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
}
