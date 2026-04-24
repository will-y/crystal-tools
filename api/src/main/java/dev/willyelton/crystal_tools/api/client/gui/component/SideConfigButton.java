package dev.willyelton.crystal_tools.api.client.gui.component;

import dev.willyelton.crystal_tools.api.common.block.entity.model.SideConfigOption;
import dev.willyelton.crystal_tools.api.utils.constants.ApiConstants;
import dev.willyelton.crystal_tools.api.utils.StringUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class SideConfigButton extends CrystalToolsButton {
    public static final Identifier TEXTURE = ApiConstants.baseRl("textures/gui/side_buttons.png");

    public static final int BUTTON_SIZE = 20;

    private final OnPressSideConfigButton onPress;
    private SideConfigOption option;
    private final Direction side;

    public SideConfigButton(int x, int y, Component name, OnPressSideConfigButton onPress, OnTooltip onToolTip, SideConfigOption initialOption, Direction side) {
        super(x, y, BUTTON_SIZE, BUTTON_SIZE, name, button -> {}, onToolTip);

        this.onPress = onPress;
        this.option = initialOption;
        this.side = side;
    }

    @Override
    protected void blitButton(GuiGraphicsExtractor guiGraphics, int textureY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), option.textureXOffset, textureY, BUTTON_SIZE, BUTTON_SIZE, 512, 512);

    }

    @Override
    protected void drawButtonText(GuiGraphicsExtractor guiGraphics, Font font, int fgColor) {

    }

    @Override
    protected int getTextureY(boolean isHovered) {
        return isHovered ? 60 : 40;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        option = onPress.onPress(this, input);
    }

    public SideConfigOption option() {
        return option;
    }

    public Direction side() {
        return side;
    }

    public Component tooltip() {
        return Component.literal(String.format("%s (%s)", StringUtils.capitalize(side.getName()), option.readableName()));
    }

    public interface OnPressSideConfigButton {
        SideConfigOption onPress(SideConfigButton button, InputWithModifiers input);
    }
}
