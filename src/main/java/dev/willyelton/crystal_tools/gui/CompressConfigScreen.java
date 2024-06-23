package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.*;

public class CompressConfigScreen extends BackpackSubScreen<CrystalBackpackContainerMenu> {
    public CompressConfigScreen(CrystalBackpackContainerMenu menu, Inventory playerInventory, CrystalBackpackScreen returnScreen) {
        super(menu, playerInventory, Component.literal("Compression"), returnScreen);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int containerRows = getContainerRows();
        int compressionRows = menu.getCompressionRows();

        // Top Bar
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, INVENTORY_WIDTH, TOP_BAR_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        for (int row = 0; row < compressionRows; row++) {
            // Compression row
            guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * row, 0, 317, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // TODO: move to super
        for (int row = 0; row < containerRows - compressionRows; row++) {
            guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * (row + compressionRows), 0, 8, INVENTORY_WIDTH, ROW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);

        }

        // Inventory
        guiGraphics.blit(TEXTURE, leftPos, topPos + TOP_BAR_HEIGHT + ROW_HEIGHT * containerRows, 0, 125, INVENTORY_WIDTH, INVENTORY_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE);
    }
}
