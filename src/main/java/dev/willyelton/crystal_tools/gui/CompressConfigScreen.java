package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.INVENTORY_HEIGHT;
import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.INVENTORY_WIDTH;
import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.ROW_HEIGHT;
import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.TEXTURE;
import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.TEXTURE_SIZE;
import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.TOP_BAR_HEIGHT;

public class CompressConfigScreen extends BackpackSubScreen<CrystalBackpackContainerMenu> {
    public CompressConfigScreen(CrystalBackpackContainerMenu menu, Inventory playerInventory, Screen returnScreen) {
        super(menu, playerInventory, Component.literal("Compression"), returnScreen);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // TODO: Location should probably match up with backpack for consistency. Calculations can be the same. Use return screen reference to get
        int rowsToDraw = 3;

        // Top Bar
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, INVENTORY_WIDTH, TOP_BAR_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        for (int row = 0; row < rowsToDraw; row++) {
            // Backpack row
            guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * row, 0, 222, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // Inventory
        guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * rowsToDraw, 0, 125, INVENTORY_WIDTH, INVENTORY_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
    }
}
