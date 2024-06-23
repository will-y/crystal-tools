package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.network.PacketHandler;
import dev.willyelton.crystal_tools.network.packet.BackpackScreenPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.*;
import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.ROW_HEIGHT;

public abstract class BackpackSubScreen<T extends CrystalBackpackContainerMenu> extends AbstractContainerScreen<T> {
    private final CrystalBackpackScreen returnScreen;

    public BackpackSubScreen(T menu, Inventory playerInventory, Component title, CrystalBackpackScreen returnScreen) {
        super(menu, playerInventory, title);
        this.returnScreen = returnScreen;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void onClose() {
        menu.closeSubScreen();
        PacketHandler.sendToServer(new BackpackScreenPacket(BackpackScreenPacket.Type.CLOSE_SUB_SCREEN));
        this.minecraft.popGuiLayer();
        this.minecraft.setScreen(this.returnScreen);
    }

    @Override
    protected void init() {
        int rowsToDraw =getContainerRows();

        this.inventoryLabelY = rowsToDraw * ROW_HEIGHT + CrystalBackpackContainerMenu.START_Y + 2;
        this.imageHeight = TOP_BAR_HEIGHT + INVENTORY_HEIGHT + rowsToDraw * ROW_HEIGHT;

        super.init();
    }

    public CrystalBackpackScreen getReturnScreen() {
        return returnScreen;
    }

    public int getContainerRows() {
        return getReturnScreen().getDisplayRows();
    }
}
