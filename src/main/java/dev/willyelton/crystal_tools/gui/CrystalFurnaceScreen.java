package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrystalFurnaceScreen extends AbstractFurnaceScreen<CrystalFurnaceContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");

    public CrystalFurnaceScreen(CrystalFurnaceContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, new SmeltingRecipeBookComponent(), pPlayerInventory, pTitle, TEXTURE);
    }
}
