package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.common.inventory.container.BaseContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenType;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.*;

public abstract class BackpackSubScreen<T extends BaseContainerMenu & SubScreenContainerMenu, U extends Screen & SubScreenContainerScreen> extends AbstractContainerScreen<T> {
    private final U returnScreen;

    public BackpackSubScreen(T menu, Inventory playerInventory, Component title, U returnScreen) {
        super(menu, playerInventory, title);
        this.returnScreen = returnScreen;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int containerRows = getContainerRows();
        // Either filter rows or compression rows
        int contentRows = getRowsToDraw();

        drawTopBar(guiGraphics);

        for (int row = 0; row < contentRows; row++) {
            drawContentRow(guiGraphics, row);
        }

        for (int row = 0; row < containerRows - contentRows; row++) {
            drawEmptyRow(guiGraphics, row + contentRows);
        }

        drawPlayerInventory(guiGraphics, containerRows);
        drawBottomBar(guiGraphics, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * containerRows + INVENTORY_HEIGHT - 4);
    }

    @Override
    public void onClose() {
        menu.closeSubScreen();
        ClientPacketDistributor.sendToServer(new BackpackScreenPayload(BackpackScreenPayload.BackpackAction.CLOSE_SUB_SCREEN));
        this.minecraft.popGuiLayer();
        this.minecraft.setScreen(this.returnScreen);
    }

    @Override
    protected void init() {
        int rowsToDraw = getContainerRows();

        this.inventoryLabelY = rowsToDraw * ROW_HEIGHT + CrystalBackpackContainerMenu.START_Y + 2;
        this.imageHeight = TOP_BAR_HEIGHT + INVENTORY_HEIGHT + rowsToDraw * ROW_HEIGHT;

        super.init();
    }

    public U getReturnScreen() {
        return returnScreen;
    }

    public int getContainerRows() {
        return getReturnScreen().getDisplayRows();
    }

    protected void drawTopBar(GuiGraphics guiGraphics) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, INVENTORY_WIDTH, TOP_BAR_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    protected void drawEmptyRow(GuiGraphics guiGraphics, int rowIndex) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * rowIndex, 0, 8, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    protected void drawPlayerInventory(GuiGraphics guiGraphics, int rowIndex) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * rowIndex, 0, 125, INVENTORY_WIDTH, INVENTORY_HEIGHT - 4, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    protected void drawBottomBar(GuiGraphics guiGraphics, int y) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, y, 0, 217, INVENTORY_WIDTH, 4, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    protected abstract int getRowsToDraw();

    protected abstract void drawContentRow(GuiGraphics guiGraphics, int row);

    public abstract Component getButtonName();

    public abstract SubScreenType getType();

    public abstract int getButtonTextureXOffset();
}
