package dev.willyelton.crystal_tools.common.inventory.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractGeneratorContainerMenu extends EnergyLevelableContainerMenu {
    public AbstractGeneratorContainerMenu(MenuType<? extends AbstractGeneratorContainerMenu> menuType, int containerId,
                                          Inventory playerInventory, ContainerData data,
                                          int playerInventoryX, int playerInventoryY) {
        super(menuType, containerId, playerInventory, data);

        this.layoutPlayerInventorySlots(playerInventoryX, playerInventoryY);
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

    public boolean isLit() {
        return this.data.get(3) > 0;
    }

    public float getLitProgress() {
        if (this.data.get(4) == 0) return 0;

        return this.data.get(3) / (float) this.data.get(4);
    }

    @Override
    public float getCurrentEnergy() {
        return this.data.get(5);
    }

    @Override
    public float getMaxEnergy() {
        return this.data.get(6);
    }

    public int getCurrentGeneration() {
        return this.data.get(7);
    }

    @Override
    public String getBlockType() {
        return "generator";
    }
}
