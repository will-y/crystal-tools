package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.common.inventory.container.CrystalPedestalContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static dev.willyelton.crystal_tools.common.inventory.container.CrystalPedestalContainerMenu.CONTAINER_ROWS;

public class CrystalPedestalScreen extends AbstractContainerScreen<CrystalPedestalContainerMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public CrystalPedestalScreen(CrystalPedestalContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);

        this.imageHeight = 114 + CONTAINER_ROWS * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, CONTAINER_ROWS * 18 + 17);
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j + CONTAINER_ROWS * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}
