package dev.willyelton.crystal_tools.gui.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class FurnaceUpgradeButton extends Button {
    public static final ResourceLocation SKILL_BUTTON_LOCATION = new ResourceLocation("crystal_tools", "textures/gui/furnace_button.png");

    private boolean hasSkillPoints;

    public FurnaceUpgradeButton(int x, int y, int width, int height, Component message, OnPress onPress, OnTooltip onTooltip, boolean hasSkillPoints) {
        super(x, y, width, height, message, onPress, onTooltip);
        this.hasSkillPoints = hasSkillPoints;
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SKILL_BUTTON_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        // need to override this based on my button texture
        int i = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        // first half of button
        this.blit(poseStack, this.x, this.y, 0, i * 20, this.width / 2, this.height);
        // second half of button
        this.blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, i * 20, this.width / 2, this.height);
        // pretty sure does nothing
        this.renderBg(poseStack, minecraft, mouseX, mouseY);
        // might need to change based off of how text looks on colors I pick
        int j = getFGColor();
        drawCenteredString(poseStack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);

        if (this.isHoveredOrFocused()) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    @Override
    protected int getYImage(boolean pIsHovered) {
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
