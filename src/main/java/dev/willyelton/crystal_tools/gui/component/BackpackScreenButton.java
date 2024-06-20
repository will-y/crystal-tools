package dev.willyelton.crystal_tools.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.TEXTURE;

public class BackpackScreenButton extends CrystalToolsButton {
    private static final int BUTTON_SIZE = 20;
    private static final int TEXTURE_Y = 335;

    private final int xTextureOffset;

    public BackpackScreenButton(int x, int y, Component name, OnPress onPress, OnTooltip onToolTip, int xTextureOffset) {
        super(x, y, BUTTON_SIZE, BUTTON_SIZE, name, onPress, onToolTip);

        this.xTextureOffset = xTextureOffset;
    }

    @Override
    protected void blitButton(GuiGraphics guiGraphics, int textureY) {
        guiGraphics.blit(TEXTURE, getX(), getY(), xTextureOffset, textureY, BUTTON_SIZE, BUTTON_SIZE, 512, 512);
    }

    @Override
    protected void drawButtonText(GuiGraphics guiGraphics, Font font, int fgColor) {

    }

    @Override
    protected int getTextureY(boolean isHovered) {
        if (this.isHovered) {
            return TEXTURE_Y + BUTTON_SIZE;
        }

        return TEXTURE_Y;
    }
}
