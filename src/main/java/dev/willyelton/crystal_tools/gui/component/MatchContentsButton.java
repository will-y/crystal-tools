package dev.willyelton.crystal_tools.gui.component;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import static dev.willyelton.crystal_tools.gui.CrystalBackpackScreen.TEXTURE;

public class MatchContentsButton extends CrystalToolsButton {
    private static final int BUTTON_SIZE = 12;
    private static final int TEXTURE_Y = 281;

    public MatchContentsButton(int x, int y, OnPress onPress, OnTooltip onToolTip) {
        super(x, y, BUTTON_SIZE, BUTTON_SIZE, Component.literal("Match Backpack Contents"), onPress, onToolTip);
    }

    @Override
    protected void blitButton(GuiGraphics guiGraphics, int textureY) {
        guiGraphics.blit(TEXTURE, getX(), getY(), 60, textureY, BUTTON_SIZE, BUTTON_SIZE, 512, 512);
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
