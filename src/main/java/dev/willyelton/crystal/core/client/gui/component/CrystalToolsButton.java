package dev.willyelton.crystal.core.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class CrystalToolsButton extends Button {
    protected final OnTooltip onTooltip;

    public CrystalToolsButton(int x, int y, int width, int height, Component name, OnPress onPress, OnTooltip onToolTip) {
        super(x, y, width, height, name, onPress, DEFAULT_NARRATION);
        this.onTooltip = onToolTip;
    }

    @Override
    public void extractContents(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int textureY = this.getTextureY(this.isHoveredOrFocused());
        this.blitButton(guiGraphics, textureY);
        int j = getFGColor();
        this.drawButtonText(guiGraphics, font, j);

        if (this.isHovered()) {
            this.onTooltip.onTooltip(this, guiGraphics, mouseX, mouseY);
        }
    }

    protected abstract void blitButton(GuiGraphicsExtractor guiGraphics, int textureY);

    protected abstract void drawButtonText(GuiGraphicsExtractor guiGraphics, Font font, int fgColor);

    protected abstract int getTextureY(boolean isHovered);

    @Override
    public int getFGColor() {
        return this.active ? 16777215 : 10526880;
    }

    public interface OnTooltip {
        void onTooltip(Button pButton, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY);
    }
}
