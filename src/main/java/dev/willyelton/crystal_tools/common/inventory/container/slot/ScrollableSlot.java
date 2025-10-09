package dev.willyelton.crystal_tools.common.inventory.container.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

// TODO: Look at ScrollPanel, maybe we don't need all of this
public class ScrollableSlot extends ResourceHandlerSlot {
    private int actualSlotIndex;
    private boolean active = true;
    private final ResourceHandler<ItemResource> handler;
    private final IndexModifier<ItemResource> slotModifier;

    public ScrollableSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> slotModifier, int index, int xPosition, int yPosition) {
        super(handler, slotModifier, index, xPosition, yPosition);
        this.actualSlotIndex = index;
        this.handler = handler;
        this.slotModifier = slotModifier;
    }

    public void setSlotIndex(int slot) {
        this.actualSlotIndex = slot;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty())
            return false;
        return handler.isValid(actualSlotIndex, ItemResource.of(stack));
    }

    @Override
    public ItemStack getStackCopy() {
        return handler.getResource(actualSlotIndex).toStack(handler.getAmountAsInt(actualSlotIndex));
    }

    // TODO: This is getting called when scrolling? Probably shouldn't be
    @Override
    public void setStackCopy(@NotNull ItemStack stack) {
        slotModifier.set(actualSlotIndex, ItemResource.of(stack), stack.getCount());
    }

    @Override
    public int getMaxStackSize() {
        return handler.getCapacityAsInt(actualSlotIndex, ItemResource.EMPTY);
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return handler.getCapacityAsInt(actualSlotIndex, ItemResource.of(stack));
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        try (var tx = Transaction.open(null)) {
            // Simulated extraction
            return handler.extract(actualSlotIndex, handler.getResource(actualSlotIndex), 1, tx) == 1;
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
