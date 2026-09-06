package dev.willyelton.crystal.core.common.inventory.container;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.common.block.entity.BaseContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import static dev.willyelton.crystal.core.client.gui.CrystalContainerScreen.*;

/**
 * A container for a basic chest / barrel
 */
public class CrystalContainerMenu extends BaseContainerMenu {
    private final BaseContainerBlockEntity blockEntity;

    // Client Constructor
    public CrystalContainerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, data.readBlockPos());
    }

    public CrystalContainerMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(Registration.CRYSTAL_CONTAINER_MENU.get(), containerId, playerInventory, null);

        blockEntity = (BaseContainerBlockEntity) level.getBlockEntity(pos);
        if (blockEntity == null) {
            return;
        }

        this.addSlotBox(blockEntity.getItemHandler(), 0, X_PADDING + 1, Y_PADDING + 1, blockEntity.getWidth(), SLOT_SIZE, blockEntity.getHeight(), SLOT_SIZE);

        this.layoutPlayerInventorySlots(calcWidth(this) / 2 - INVENTORY_TEXTURE_SIZE_X / 2 + 1, calcHeight(this) - INVENTORY_TEXTURE_SIZE_Y - Y_PADDING + 11);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        int size = this.slotWidth() * this.slotHeight();

        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex < size) {
                if (!this.moveItemStackTo(stack, size, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, size, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        if (level != null && level.getBlockEntity(blockEntity.getBlockPos()) != blockEntity) {
            return false;
        } else {
            return player.distanceToSqr((double) blockEntity.getBlockPos().getX() + 0.5D, (double) blockEntity.getBlockPos().getY() + 0.5D, (double) blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    public int slotWidth() {
        return blockEntity.getWidth();
    }

    public int slotHeight() {
        return blockEntity.getHeight();
    }

    public int inventoryStartX() {
        return 200;
    }
    public int inventoryStartY() {
        return 100;
    }

    public BaseContainerBlockEntity blockEntity() {
        return blockEntity;
    }
}
