package dev.willyelton.crystal_tools.common.inventory.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;

public abstract class EnergyLevelableContainerMenu extends LevelableContainerMenu {
    protected EnergyLevelableContainerMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, ContainerData data) {
        super(menuType, containerId, playerInventory, data);
    }

    public abstract float getCurrentEnergy();

    public abstract float getMaxEnergy();
}
