package dev.willyelton.crystal_tools.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.TEXTURE;

public class WhitelistToggleButton extends CrystalToolsButton {
    private static final int BUTTON_SIZE = 20;
    private static final int TEXTURE_Y = 281;

    boolean isWhitelist;
    boolean isDisabled;

    public WhitelistToggleButton(int x, int y, OnPress onPress, OnTooltip onToolTip, boolean isWhitelist, boolean isDisabled) {
        super(x, y, BUTTON_SIZE, BUTTON_SIZE, Component.literal("Whitelist"), onPress, onToolTip);
        this.isWhitelist = isWhitelist;
        this.isDisabled = isDisabled;
    }

    @Override
    protected void blitButton(GuiGraphics guiGraphics, int textureY) {
        int xOffset = isWhitelist ? 0 : BUTTON_SIZE;
        guiGraphics.blit(TEXTURE, getX(), getY(), xOffset, textureY, BUTTON_SIZE, BUTTON_SIZE, 512, 512);
    }

    @Override
    protected void drawButtonText(GuiGraphics guiGraphics, Font font, int fgColor) {

    }

    @Override
    protected int getTextureY(boolean isHovered) {
        if (isDisabled) {
            return TEXTURE_Y + BUTTON_SIZE * 2;
        }

        if (isHovered) {
            return TEXTURE_Y + BUTTON_SIZE;
        }

        return TEXTURE_Y;
    }

    public void setWhitelist(boolean whitelist) {
        this.isWhitelist = whitelist;
    }
}
