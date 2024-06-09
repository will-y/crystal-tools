package dev.willyelton.crystal_tools.client.gui.component;

import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FurnaceUpgradeButton extends CrystalToolsButton {
    public static final ResourceLocation FURNACE_BUTTON_LOCATION = new ResourceLocation(CrystalTools.MODID, "textures/gui/furnace_button.png");
    private final boolean hasSkillPoints;

    public FurnaceUpgradeButton(int x, int y, int width, int height, Component message, OnPress onPress, SkillButton.OnTooltip onTooltip, boolean hasSkillPoints) {
        super(x, y, width, height, message, onPress, onTooltip);
        this.hasSkillPoints = hasSkillPoints;
    }

    @Override
    protected void blitButton(GuiGraphics guiGraphics, int textureY) {
        // first half of button
        guiGraphics.blit(FURNACE_BUTTON_LOCATION, this.getX(), this.getY(), 0, textureY * this.height, this.width / 2, this.height);
        // second half of button
        guiGraphics.blit(FURNACE_BUTTON_LOCATION, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, textureY * this.height, this.width / 2, this.height);
    }

    @Override
    protected void drawButtonText(GuiGraphics guiGraphics, Font font, int fgColor) {
        guiGraphics.drawCenteredString(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, fgColor | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    protected int getTextureY(boolean pIsHovered) {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (pIsHovered && this.hasSkillPoints) {
            i = 4;
        } else if (pIsHovered) {
            i = 2;
        } else if (this.hasSkillPoints) {
            i = 3;
        }

        return i;
    }
}
