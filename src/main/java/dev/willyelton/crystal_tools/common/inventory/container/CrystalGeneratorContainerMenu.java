package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CrystalGeneratorContainerMenu extends AbstractGeneratorContainerMenu {
    private final CrystalGeneratorBlockEntity blockEntity;

    public CrystalGeneratorContainerMenu(int containerId, Level level, BlockPos pos, Inventory playerInventory, ContainerData data) {
        super(Registration.CRYSTAL_GENERATOR_CONTAINER.get(), containerId, playerInventory, data, 8, 109);
        blockEntity = (CrystalGeneratorBlockEntity) level.getBlockEntity(pos);

        if (blockEntity == null) {
            return;
        }

        this.addSlot(blockEntity.getFuelHandler(), 0, 80, 59);
    }

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

    @Override
    public boolean stillValid(Player player) {
        if (level != null && level.getBlockEntity(blockEntity.getBlockPos()) != blockEntity) {
            return false;
        } else {
            return player.distanceToSqr((double) blockEntity.getBlockPos().getX() + 0.5D, (double) blockEntity.getBlockPos().getY() + 0.5D, (double) blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public CrystalGeneratorBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
