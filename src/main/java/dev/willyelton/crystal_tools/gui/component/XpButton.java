package dev.willyelton.crystal_tools.gui.component;


import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.utils.Colors;
import dev.willyelton.crystal_tools.utils.XpUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Button to require xp costs
 */
public class XpButton extends Button {
    private static ResourceLocation TEXTURE = new ResourceLocation(CrystalTools.MODID, "textures/gui/xp_button.png");
    protected final CrystalToolsButton.OnTooltip onTooltip;

    public XpButton(int x, int y, int width, int height, OnPress onPress, CrystalToolsButton.OnTooltip onToolTip, int levelCost) {
        super(x, y, width, height, Component.literal("" + levelCost), onPress, DEFAULT_NARRATION);
        this.onTooltip = onToolTip;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(TEXTURE, getX() + 4, getY() + 5, 0, 0, 9, 9);

        if (this.isHovered) {
            this.onTooltip.onTooltip(this, guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int pColor) {
        int color = this.active ? Colors.fromRGB(200, 255, 143) : Colors.fromRGB(140, 96, 93);
        int textWidth = font.width(this.getMessage());
        guiGraphics.drawString(font, this.getMessage(), this.getX() + 16, this.getY() + (getHeight() - font.lineHeight) / 2 + 1, color);
    }

    public void update(int levelCost, Player player) {
        active = XpUtils.getPlayerTotalXp(player) >= XpUtils.getXPForLevel(levelCost);
        this.setMessage(Component.literal("" + levelCost));
    }
}
