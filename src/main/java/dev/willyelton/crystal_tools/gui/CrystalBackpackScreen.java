package dev.willyelton.crystal_tools.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CrystalBackpackScreen extends AbstractContainerScreen<CrystalBackpackContainerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("crystal_tools:textures/gui/crystal_backpack.png");

    private static final int TEXTURE_SIZE = 512;

    private final CrystalBackpackContainerMenu container;

    public CrystalBackpackScreen(CrystalBackpackContainerMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
        this.container = container;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = container.getRows() * 18 + CrystalBackpackContainerMenu.START_Y + 2;
    }

    // TODO: Draw filter text
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        // Backpack top bar
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, 176, 17, TEXTURE_SIZE, TEXTURE_SIZE);

        for (int row = 0; row < container.getRows(); row++) {
            // Backpack row
            guiGraphics.blit(TEXTURE, leftPos, topPos + 17 + 18 * row, 0, 222, 176, 18, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // Filter
        // TODO: Multiple filter rows
        if (container.getFilterRows() > 0) {
            guiGraphics.blit(TEXTURE, leftPos + 173, topPos, 0, 240, 103, 41, TEXTURE_SIZE, TEXTURE_SIZE);
        }

        // Player inventory
        guiGraphics.blit(TEXTURE, leftPos, topPos + 17 + 18 * container.getRows(), 0, 125, 176, 96, TEXTURE_SIZE, TEXTURE_SIZE);
    }
}
