package dev.willyelton.crystal_tools.client.gui.component.backpack;

import dev.willyelton.crystal_tools.client.gui.component.CrystalToolsButton;
import dev.willyelton.crystal_tools.utils.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TEXTURE;

public class BackpackScreenButton extends CrystalToolsButton {
    private static final int BUTTON_SIZE = 20;
    private static final int TEXTURE_Y = 335;

    private final int xTextureOffset;
    private final Font font;

    private int badgeCounter = 0;

    public BackpackScreenButton(int x, int y, Component name, OnPress onPress, OnTooltip onToolTip, int xTextureOffset) {
        super(x, y, BUTTON_SIZE, BUTTON_SIZE, name, onPress, onToolTip);

        this.xTextureOffset = xTextureOffset;
        this.font = Minecraft.getInstance().font;
    }

    @Override
    protected void blitButton(GuiGraphics guiGraphics, int textureY) {
        guiGraphics.blit(RenderType::guiTextured, TEXTURE, getX(), getY(), xTextureOffset, textureY, BUTTON_SIZE, BUTTON_SIZE, 512, 512);
        if (badgeCounter > 0) {
            String badgeCounterString = String.valueOf(badgeCounter);
            int xOffset = font.width(badgeCounterString);
            int badgeWidth = xOffset + xOffset % 2 + 4;
            guiGraphics.blit(RenderType::guiTextured, TEXTURE, getX() - badgeWidth + 2 + 5, getY() - 3, 0, 375, badgeWidth / 2, 10, 512, 512);
            guiGraphics.blit(RenderType::guiTextured, TEXTURE, getX() - badgeWidth / 2 + 7, getY() - 3, 66 - badgeWidth / 2.0F, 375, badgeWidth / 2, 10, 512, 512);
            guiGraphics.drawString(Minecraft.getInstance().font, badgeCounterString, getX() - xOffset + 5, getY() - 2, Colors.fromRGB(255, 255, 255), true);
        }
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

    public void setBadgeCounter(int badgeCounter) {
        this.badgeCounter = badgeCounter;
    }
}
