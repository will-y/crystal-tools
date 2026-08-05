package dev.willyelton.crystal.core.client.gui;

import dev.willyelton.crystal.core.common.inventory.container.CrystalContainerMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static dev.willyelton.crystal.core.utils.constants.ApiConstants.baseRl;
import static net.minecraft.world.inventory.AbstractContainerMenu.SLOT_SIZE;

///  Base container screen for any container that is just a
/// rectangle of storage slots (chest, barrel, shulker box).
/// Fits into the max gui scale up to 18 x 9
public class CrystalContainerScreen extends AbstractContainerScreen<CrystalContainerMenu> {
    public static final Identifier TEXTURE = baseRl("textures/gui/crystal_container.png");

    private static final int TEXTURE_SIZE = 512;
    private static final int INVENTORY_TEXTURE_X = 0;
    private static final int INVENTORY_TEXTURE_Y = 436;
    public static final int INVENTORY_TEXTURE_SIZE_X = 162;
    public static final int INVENTORY_TEXTURE_SIZE_Y = 76;

    public static final int Y_PADDING = 15;
    public static final int X_PADDING = 5;

    private final int contentWidth;
    private final int contentHeight;

    public CrystalContainerScreen(CrystalContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, calcWidth(menu), calcHeight(menu));

        this.contentWidth = calcWidth(menu);
        this.contentHeight = calcHeight(menu);
        this.inventoryLabelY = topPos + calcHeight(menu) - INVENTORY_TEXTURE_SIZE_Y - Y_PADDING + 1;
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.extractTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Top Left
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, contentWidth / 2, contentHeight / 2, TEXTURE_SIZE, TEXTURE_SIZE);

        // Top Right
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + contentWidth / 2, topPos, TEXTURE_SIZE - contentWidth / 2.0F, 0, contentWidth / 2, contentHeight / 2, TEXTURE_SIZE, TEXTURE_SIZE);

        // Bottom Left
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos + contentHeight / 2, 0, INVENTORY_TEXTURE_Y - contentHeight / 2.0F, contentWidth / 2, contentHeight / 2, TEXTURE_SIZE, TEXTURE_SIZE);

        // Bottom Right
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + contentWidth / 2, topPos + contentHeight / 2, TEXTURE_SIZE - contentWidth / 2.0F, INVENTORY_TEXTURE_Y - contentHeight / 2.0F, contentWidth / 2, contentHeight / 2, TEXTURE_SIZE, TEXTURE_SIZE);

        // Slots
        for (int row = 0; row < menu.slotHeight(); row++) {
            drawSlotRow(guiGraphics, leftPos + X_PADDING, topPos + row * SLOT_SIZE + Y_PADDING, menu.slotWidth());
        }

        // Inventory slots
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + contentWidth / 2 - INVENTORY_TEXTURE_SIZE_X / 2, topPos + inventoryLabelY + 9, INVENTORY_TEXTURE_X, INVENTORY_TEXTURE_Y, INVENTORY_TEXTURE_SIZE_X, INVENTORY_TEXTURE_SIZE_Y, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    private void drawSlotRow(GuiGraphicsExtractor guiGraphics, int x, int y, int slots) {
        int slotCols = slots / 9;
        int leftOver = slots % 9;
        int currentX = x;

        for (int i = 0; i < slotCols; i++) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, currentX, y, INVENTORY_TEXTURE_X, INVENTORY_TEXTURE_Y, INVENTORY_TEXTURE_SIZE_X, SLOT_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
            currentX += INVENTORY_TEXTURE_SIZE_X;
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, currentX, y, INVENTORY_TEXTURE_X, INVENTORY_TEXTURE_Y, leftOver * SLOT_SIZE, SLOT_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    public static int calcWidth(CrystalContainerMenu menu) {
        return Math.max(menu.slotWidth(), 9) * SLOT_SIZE + X_PADDING * 2;
    }

    public static int calcHeight(CrystalContainerMenu menu) {
        return menu.slotHeight() * SLOT_SIZE + Y_PADDING * 2 + INVENTORY_TEXTURE_SIZE_Y;
    }
}
