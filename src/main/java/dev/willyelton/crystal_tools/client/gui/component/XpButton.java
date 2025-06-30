package dev.willyelton.crystal_tools.client.gui.component;


import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.utils.Colors;
import dev.willyelton.crystal_tools.utils.XpUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

/**
 * Button to require xp costs
 */
public class XpButton extends Button {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/gui/xp_button.png");

    protected final CrystalToolsButton.OnTooltip onTooltip;
    protected final Supplier<Integer> levelCostSupplier;

    public XpButton(int x, int y, int width, int height, OnPress onPress, CrystalToolsButton.OnTooltip onToolTip, Supplier<Integer> levelCostSupplier) {
        super(x, y, width, height, Component.literal("" + levelCostSupplier.get()), onPress, DEFAULT_NARRATION);
        this.onTooltip = onToolTip;
        this.levelCostSupplier = levelCostSupplier;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX() + 4, getY() + 5, 0, 0, 9, 9, 256, 256);

        if (this.isHovered) {
            this.onTooltip.onTooltip(this, guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public void renderString(GuiGraphics guiGraphics, Font font, int pColor) {
        int color = this.active ? Colors.fromRGB(200, 255, 143) : Colors.fromRGB(140, 96, 93);
        guiGraphics.drawString(font, this.getMessage(), this.getX() + 16, this.getY() + (getHeight() - font.lineHeight) / 2 + 1, color);
    }

    @Override
    public Component getMessage() {
        return Component.literal("" + levelCostSupplier.get());
    }

    public void update(int levelCost, Player player) {
        active = XpUtils.getPlayerTotalXp(player) >= levelCost;
        this.setMessage(Component.literal("" + levelCost));
    }
}
