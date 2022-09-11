package dev.willyelton.crystal_tools.levelable.block.container;

import dev.willyelton.crystal_tools.levelable.block.ModBlocks;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.RecipeType;

public class CrystalFurnaceContainer extends AbstractFurnaceMenu {
    public CrystalFurnaceContainer(int pContainerId, Inventory pPlayerInventory) {
        super(ModBlocks.CRYSTAL_FURNACE_CONTAINER.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, pContainerId, pPlayerInventory);
    }

    public CrystalFurnaceContainer(int pContainerId, Inventory pPlayerInventory, Container pFurnaceContainer, ContainerData pFurnaceData) {
        super(ModBlocks.CRYSTAL_FURNACE_CONTAINER.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, pContainerId, pPlayerInventory, pFurnaceContainer, pFurnaceData);
    }
}
