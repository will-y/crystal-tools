package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalBackpackContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.INVENTORY_HEIGHT;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.ROW_HEIGHT;
import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TOP_BAR_HEIGHT;

public abstract class BackpackSubScreen<T extends CrystalBackpackContainerMenu> extends AbstractContainerScreen<T> {
    private final CrystalBackpackScreen returnScreen;

    public BackpackSubScreen(T menu, Inventory playerInventory, Component title, CrystalBackpackScreen returnScreen) {
        super(menu, playerInventory, title);
        this.returnScreen = returnScreen;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void onClose() {
        menu.closeSubScreen();
        PacketDistributor.sendToServer(new BackpackScreenPayload(BackpackScreenPayload.BackpackAction.CLOSE_SUB_SCREEN));
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
