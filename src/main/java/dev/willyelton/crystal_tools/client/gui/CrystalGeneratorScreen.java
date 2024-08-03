package dev.willyelton.crystal_tools.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalGeneratorContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

// TODO: Superclass for this and furnace could be useful. Drawing fire + animations?
public class CrystalGeneratorScreen extends AbstractContainerScreen<CrystalGeneratorContainerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/crystal_generator.png");

    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 23;
    private static final int ENERGY_WIDTH = 160;
    private static final int ENERGY_HEIGHT = 10;
    private static final int ENERGY_TEXTURE_X = 0;
    private static final int ENERGY_TEXTURE_Y = 191;

    private static final int FIRE_X = 82;
    private static final int FIRE_Y = 78;
    private static final int FIRE_WIDTH = 14;
    private static final int FIRE_HEIGHT = 13;
    private static final int FIRE_TEXTURE_ON_X = 176;
    private static final int FIRE_TEXTURE_OFF_X = 190;
    private static final int FIRE_TEXTURE_Y = 0;

    public CrystalGeneratorScreen(CrystalGeneratorContainerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);

        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.renderEnergyBar(guiGraphics);
        this.renderFire(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        // TODO: Abstract the energy bar component?
        int x1 = this.leftPos + ENERGY_X;
        int x2 = x1 + ENERGY_WIDTH;
        int y1 = this.topPos + ENERGY_Y;
        int y2 = y1 + ENERGY_HEIGHT;

        if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) {
            String tooltipString = String.format("%d/%d FE", (int) menu.getCurrentEnergy(), (int) menu.getMaxEnergy());
            guiGraphics.renderTooltip(this.font, Component.literal(tooltipString), mouseX, mouseY);
        }
    }

    private void renderEnergyBar(GuiGraphics guiGraphics) {
        float energyProgress = this.menu.getCurrentEnergy() / this.menu.getMaxEnergy();
        int width = (int) (energyProgress * ENERGY_WIDTH);
        guiGraphics.blit(TEXTURE, this.leftPos + ENERGY_X, this.topPos + ENERGY_Y, ENERGY_TEXTURE_X, ENERGY_TEXTURE_Y, width, ENERGY_HEIGHT);
    }

    private void renderFire(GuiGraphics guiGraphics) {
        guiGraphics.blit(TEXTURE, this.leftPos + FIRE_X, this.topPos + FIRE_Y, FIRE_TEXTURE_OFF_X, FIRE_TEXTURE_Y, FIRE_WIDTH, FIRE_HEIGHT);

        if (this.menu.isLit()) {
            float litProgress = this.menu.getLitProgress();
            int height = (int) (litProgress * FIRE_HEIGHT);
            guiGraphics.blit(TEXTURE, this.leftPos + FIRE_X, this.topPos + FIRE_Y + FIRE_HEIGHT - height, FIRE_TEXTURE_ON_X, FIRE_TEXTURE_Y + FIRE_HEIGHT - height, FIRE_WIDTH, height);
        }
    }
}
