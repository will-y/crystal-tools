package dev.willyelton.crystal_tools.common.inventory.container.slot.backpack;

import dev.willyelton.crystal_tools.common.inventory.container.slot.CrystalSlotItemHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class CompressionOutputSlot extends CrystalSlotItemHandler {
    private CompressionInputSlot inputSlot;

    public CompressionOutputSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> indexModifier, int index, int xPosition, int yPosition) {
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

    public void onClicked(ItemStack stack) {
        this.set(ItemStack.EMPTY);
        this.setChanged();

        if (inputSlot != null) {
            inputSlot.set(ItemStack.EMPTY);
            inputSlot.setChanged();
        }
    }

    public void setInputSlot(CompressionInputSlot inputSlot) {
        this.inputSlot = inputSlot;
    }
}