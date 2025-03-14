package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.INVENTORY_WIDTH;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.ROW_HEIGHT;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TEXTURE;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TEXTURE_SIZE;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TOP_BAR_HEIGHT;

public class CompressConfigScreen extends BackpackSubScreen<CrystalBackpackContainerMenu, CrystalBackpackScreen> {
    public CompressConfigScreen(CrystalBackpackContainerMenu menu, Inventory playerInventory, CrystalBackpackScreen returnScreen) {
        super(menu, playerInventory, Component.literal("Compression"), returnScreen);
    }

    @Override
    protected int getRowsToDraw() {
        return menu.getCompressionRows();
    }

    @Override
    protected void drawContentRow(GuiGraphics guiGraphics, int row) {
        guiGraphics.blit(RenderType::guiTextured, TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * row, 0, 317, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    @Override
    public Component getButtonName() {
        return Component.literal("Configure Compressions");
    }

    @Override
    public SubScreenType getType() {
        return SubScreenType.COMPRESS;
    }

    @Override
    public int getButtonTextureXOffset() {
        return 20;
    }
}
