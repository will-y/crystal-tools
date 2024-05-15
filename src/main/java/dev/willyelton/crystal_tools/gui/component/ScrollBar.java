package dev.willyelton.crystal_tools.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ScrollBar extends AbstractWidget {
    private static final ResourceLocation TEXTURE = new ResourceLocation("crystal_tools:textures/gui/scroll_bar.png");

    private static final int TEXTURE_SIZE = 128;

    private final int handleSize;
    private int position;

    public ScrollBar(int x, int y, int height, int handleSize) {
        super(x, y, 6, height, Component.literal("Scroll"));
        this.handleSize = handleSize;
        this.position = 0;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Draw background
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, 0, width, height, TEXTURE_SIZE, TEXTURE_SIZE);

        // Draw handle
        guiGraphics.blit(TEXTURE, this.getX() + 1, this.getY() + 1 + position, 6, 0, 4, handleSize, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        int drag = (int) dragY;

        if (position + handleSize + drag > height) {
            position = height - handleSize;
        } else if (position + drag < 0) {
            position = 0;
        } else {
            position += drag;
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        this.setFocused(true);
        return true;
    }
}
