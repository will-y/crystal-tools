package dev.willyelton.crystal_tools.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public abstract class CrystalToolsButton extends Button {

    protected final OnTooltip onTooltip;

    public CrystalToolsButton(int x, int y, int width, int height, Component name, OnPress onPress, OnTooltip onToolTip) {
        super(x, y, width, height, name, onPress, DEFAULT_NARRATION);
        this.onTooltip = onToolTip;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int textureY = this.getTextureY(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        this.blitButton(guiGraphics, textureY);
        int j = getFGColor();
        this.drawButtonText(guiGraphics, font, j);

        if (this.isHovered()) {
            this.onTooltip.onTooltip(this, guiGraphics, mouseX, mouseY);
        }
    }

    protected abstract void blitButton(GuiGraphics guiGraphics, int textureY);

    protected abstract void drawButtonText(GuiGraphics guiGraphics, Font font, int fgColor);

    protected abstract int getTextureY(boolean isHovered);

    @OnlyIn(Dist.CLIENT)
    public interface OnTooltip {
        void onTooltip(Button pButton, GuiGraphics guiGraphics, int mouseX, int mouseY);
    }
}
