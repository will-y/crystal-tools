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
    public static final ResourceLocation SKILL_BUTTON_LOCATION = new ResourceLocation("crystal_tools", "textures/gui/furnace_button.png");

    protected final OnTooltip onTooltip;

    public CrystalToolsButton(int x, int y, int width, int height, Component name, OnPress onPress, OnTooltip onToolTip) {
        super(x, y, width, height, name, onPress, DEFAULT_NARRATION);
        this.onTooltip = onToolTip;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SKILL_BUTTON_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        // need to override this based on my button texture
        int textureY = this.getTextureY(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blitButton(guiGraphics, textureY);
        // pretty sure does nothing TODO: CHECK
//        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        // might need to change based off of how text looks on colors I pick
        int j = getFGColor();
        this.drawButtonText(guiGraphics, font, j);

        if (this.isHoveredOrFocused()) {
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
