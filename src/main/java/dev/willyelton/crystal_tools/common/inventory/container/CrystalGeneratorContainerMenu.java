package dev.willyelton.crystal_tools.common.inventory.container;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.levelable.block.entity.CrystalGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// TODO: Subclass of just furnace and generators (not backpack)
public class CrystalGeneratorContainerMenu extends LevelableContainerMenu {
    private final CrystalGeneratorBlockEntity blockEntity;

    public CrystalGeneratorContainerMenu(int containerId, Level level, BlockPos pos, Inventory playerInventory, ContainerData data) {
        super(Registration.CRYSTAL_GENERATOR_CONTAINER.get(), containerId, playerInventory, data);
        blockEntity = (CrystalGeneratorBlockEntity) level.getBlockEntity(pos);

        if (blockEntity == null) {
            return;
        }

        this.addSlot(blockEntity.getFuelHandler(), 0, 80, 59);

        this.layoutPlayerInventorySlots(8, 109);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // TODO
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        if (level != null && level.getBlockEntity(blockEntity.getBlockPos()) != blockEntity) {
            return false;
        } else {
            return player.distanceToSqr((double) blockEntity.getBlockPos().getX() + 0.5D, (double) blockEntity.getBlockPos().getY() + 0.5D, (double) blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean isLit() {
        return this.data.get(3) > 0;
    }

    public float getLitProgress() {
        if (this.data.get(4) == 0) return 0;

        return this.data.get(3) / (float) this.data.get(4);
    }

    public float getCurrentEnergy() {
        return this.data.get(5);
    }

    public float getMaxEnergy() {
        return this.data.get(6);
    }

    @Override
    public String getBlockType() {
        return "generator";
    }
}
