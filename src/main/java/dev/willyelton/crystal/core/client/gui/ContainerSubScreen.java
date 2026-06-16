package dev.willyelton.crystal.core.client.gui;

import dev.willyelton.crystal.core.common.inventory.container.BaseContainerMenu;
import dev.willyelton.crystal.core.common.inventory.container.SubScreenType;
import dev.willyelton.crystal.core.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal.core.common.network.payload.OpenSubScreenPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;

public abstract class ContainerSubScreen<T extends BaseContainerMenu & SubScreenContainerMenu, U extends Screen & SubScreenContainerScreen> extends AbstractContainerScreen<T> {
    public static final Identifier TEXTURE = baseRl("textures/gui/crystal_backpack.png");
    public static final int TEXTURE_SIZE = 512;
    public static final int INVENTORY_WIDTH = 176;
    public static final int INVENTORY_HEIGHT = 96;
    public static final int TOP_BAR_HEIGHT = 17;
    public static final int ROW_HEIGHT = 18;
    public static final int START_Y = 18;

    private final U returnScreen;

    public ContainerSubScreen(T menu, Inventory playerInventory, Component title, U returnScreen) {
        super(menu, playerInventory, title);
        this.returnScreen = returnScreen;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.extractTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
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
        ClientPacketDistributor.sendToServer(new OpenSubScreenPayload(SubScreenType.NONE));
        if (this.returnScreen != null) {
            this.minecraft.gui.popScreenLayer();
            this.minecraft.setScreenAndShow(this.returnScreen);
        } else {
            super.onClose();
        }
    }

    @Override
    protected void init() {
        int rowsToDraw = getContainerRows();

        this.inventoryLabelY = rowsToDraw * ROW_HEIGHT + START_Y + 2;
        this.imageHeight = TOP_BAR_HEIGHT + INVENTORY_HEIGHT + rowsToDraw * ROW_HEIGHT;

        super.init();
    }

    public U getReturnScreen() {
        return returnScreen;
    }

    public int getContainerRows() {
        if (returnScreen == null) return 3;

        return getReturnScreen().getDisplayRows();
    }

    protected void drawTopBar(GuiGraphicsExtractor guiGraphics) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, INVENTORY_WIDTH, TOP_BAR_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    protected void drawEmptyRow(GuiGraphicsExtractor guiGraphics, int rowIndex) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * rowIndex, 0, 8, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    protected void drawPlayerInventory(GuiGraphicsExtractor guiGraphics, int rowIndex) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * rowIndex, 0, 125, INVENTORY_WIDTH, INVENTORY_HEIGHT - 4, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    protected void drawBottomBar(GuiGraphicsExtractor guiGraphics, int y) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, y, 0, 217, INVENTORY_WIDTH, 4, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    protected int getRowsToDraw() {
        return 0;
    }

    protected void drawContentRow(GuiGraphicsExtractor guiGraphics, int row) {

    }

    public abstract Component getButtonName();

    public abstract SubScreenType getType();

    public abstract int getButtonTextureXOffset();
}
