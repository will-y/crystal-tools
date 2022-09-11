package dev.willyelton.crystal_tools.levelable.block.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceMenu;

public class CrystalFurnaceContainer extends FurnaceMenu {
    public CrystalFurnaceContainer(int pContainerId, Inventory pPlayerInventory) {
        super(pContainerId, pPlayerInventory);
    }

    public CrystalFurnaceContainer(int pContainerId, Inventory pPlayerInventory, Container pFurnaceContainer, ContainerData pFurnaceData) {
        super(pContainerId, pPlayerInventory, pFurnaceContainer, pFurnaceData);
    }
}
