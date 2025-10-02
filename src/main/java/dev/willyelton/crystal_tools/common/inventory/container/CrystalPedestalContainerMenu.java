package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CrystalPedestalContainerMenu extends BaseContainerMenu {
    public static final int CONTAINER_ROWS = 3;

    private final CrystalPedestalBlockEntity blockEntity;

    // Client Constructor
    public CrystalPedestalContainerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(containerId, playerInventory, data.readBlockPos());
    }

    // Server Constructor
    public CrystalPedestalContainerMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(ModRegistration.CRYSTAL_PEDESTAL_CONTAINER.get(), containerId, playerInventory, null);
        Level level = playerInventory.player.level();
        blockEntity = (CrystalPedestalBlockEntity) level.getBlockEntity(pos);

        if (blockEntity == null) return;

        this.addSlotBox(blockEntity.getContentsHandler(), 0, 8, 18, 9, 18, 3, 18);
        this.layoutPlayerInventorySlots(8, 85);
    }

    // Just basic vanilla one from ChestMenu
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < CONTAINER_ROWS * 9) {
                if (!this.moveItemStackTo(itemstack1, CONTAINER_ROWS * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, CONTAINER_ROWS * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (level != null && level.getBlockEntity(blockEntity.getBlockPos()) != blockEntity) {
            return false;
        } else {
            return player.distanceToSqr((double) blockEntity.getBlockPos().getX() + 0.5D, (double) blockEntity.getBlockPos().getY() + 0.5D, (double) blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }
}
