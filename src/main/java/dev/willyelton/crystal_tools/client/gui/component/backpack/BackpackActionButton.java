package dev.willyelton.crystal_tools.client.gui.component.backpack;

import dev.willyelton.crystal_tools.client.gui.component.CrystalToolsButton;
import dev.willyelton.crystal_tools.common.inventory.container.subscreen.SubScreenContainerMenu;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen.TEXTURE;

public abstract class BackpackActionButton extends CrystalToolsButton {
    private static final int BUTTON_SIZE = 12;
    private static final int TEXTURE_Y = 281;

    private final int uOffset;

    public BackpackActionButton(int x, int y, Component name, BackpackScreenPayload.BackpackAction action, int uOffset, Screen screen, SubScreenContainerMenu container) {
        this(x, y, name, button -> container.sendUpdatePacket(action, Screen.hasShiftDown()), (button, guiGraphics, mouseX, mouseY) -> {
            guiGraphics.renderTooltip(screen.getMinecraft().font, screen.getMinecraft().font.split(name, Math.max(screen.width / 2 - 43, 170)), mouseX, mouseY);
        }, uOffset);
    }

    public BackpackActionButton(int x, int y, Component name, OnPress onPress, OnTooltip onTooltip, int uOffset) {
        super(x, y, BUTTON_SIZE, BUTTON_SIZE, name, onPress, onTooltip);
        this.uOffset = uOffset;
    }

    @Override
    protected void blitButton(GuiGraphics guiGraphics, int textureY) {
        guiGraphics.blit(TEXTURE, getX(), getY(), getUOffset(), textureY, BUTTON_SIZE, BUTTON_SIZE, 512, 512);
    }

    @Override
    protected void drawButtonText(GuiGraphics guiGraphics, Font font, int fgColor) {

    }

    @Override
    protected int getTextureY(boolean isHovered) {
        // I don't like how it stays focused when clicked
        if (this.isHovered) {
            return TEXTURE_Y + BUTTON_SIZE;
        }

        return TEXTURE_Y;
    }

    protected int getUOffset() {
        return this.uOffset;
    }
}
