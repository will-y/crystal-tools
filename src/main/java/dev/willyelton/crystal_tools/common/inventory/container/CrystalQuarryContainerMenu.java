package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalQuarryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CrystalQuarryContainerMenu extends EnergyLevelableContainerMenu {
    private final CrystalQuarryBlockEntity blockEntity;

    public CrystalQuarryContainerMenu(int containerId, Level level, BlockPos pos, Inventory playerInventory, ContainerData data) {
        super(Registration.CRYSTAL_QUARRY_CONTAINER.get(), containerId, playerInventory, data);
        blockEntity = (CrystalQuarryBlockEntity) level.getBlockEntity(pos);

        if (blockEntity == null) return;

        this.addSlotBox(blockEntity.getItemHandler(), 0, 8, 59, 9, SLOT_SIZE, 3, SLOT_SIZE);
        this.layoutPlayerInventorySlots(8, 145);
    }

    // TODO: Default implementation
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            // Fuel Slot
            if (index == 0) {
                if (!this.moveItemStackTo(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    // TODO: Default implementation (generic block entity type in BaseContainerMenu)
    @Override
    public boolean stillValid(Player player) {
        if (level != null && level.getBlockEntity(blockEntity.getBlockPos()) != blockEntity) {
            return false;
        } else {
            return player.distanceToSqr((double) blockEntity.getBlockPos().getX() + 0.5D, (double) blockEntity.getBlockPos().getY() + 0.5D, (double) blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public String getBlockType() {
        return "quarry";
    }

    // TODO: Probably not needed if i add block entity to base container menu
    @Override
    public CrystalQuarryBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public float getCurrentEnergy() {
        return this.data.get(3);
    }

    @Override
    public float getMaxEnergy() {
        return this.data.get(4);
    }
}
