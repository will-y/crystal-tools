package dev.willyelton.crystal_tools.client.gui.component;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.inventory.container.EnergyLevelableContainerMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnergyBarWidget extends AbstractWidget {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID ,"textures/gui/energy_bar.png");

    private static final int ENERGY_TEXTURE_X = 0;
    private static final int ENERGY_TEXTURE_Y = 12;

    private final Font font;
    private final EnergyLevelableContainerMenu menu;

    public EnergyBarWidget(int x, int y, int width, int height, Component message, Font font, EnergyLevelableContainerMenu menu) {
        super(x, y, width, height, message);

        this.font = font;
        this.menu = menu;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderEnergyBar(guiGraphics);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x1 = this.getX();
        int x2 = x1 + this.width;
        int y1 = this.getY();
        int y2 = y1 + this.height;

        if (mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2) {
            String tooltipString = String.format("%d/%d FE", (int) menu.getCurrentEnergy(), (int) menu.getMaxEnergy());
            guiGraphics.setTooltipForNextFrame(this.font, Component.literal(tooltipString), mouseX, mouseY);
        }
    }

    // TODO: Maybe draw the blank outline here, need to draw both halves like button
    // Animations
    private void renderEnergyBar(GuiGraphics guiGraphics) {
        float energyProgress = this.menu.getCurrentEnergy() / this.menu.getMaxEnergy();
        int width = (int) (energyProgress * this.width);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), ENERGY_TEXTURE_X, ENERGY_TEXTURE_Y, width, this.height, 256, 256);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
