package dev.willyelton.crystal_tools.client.gui.component.backpack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WhitelistToggleButton extends BackpackActionButton {
    boolean isWhitelist;

    public WhitelistToggleButton(int x, int y, OnPress onPress, boolean isWhitelist, Screen screen) {
        super(x, y, Component.literal("Whitelist"), onPress, (button, guiGraphics, mouseX, mouseY) -> {
            if (button instanceof WhitelistToggleButton toggleButton) {
                Component tooltip = toggleButton.isWhitelist ? Component.literal("Whitelist") : Component.literal("Blacklist");
                guiGraphics.setTooltipForNextFrame(screen.getMinecraft().font, screen.getMinecraft().font.split(tooltip, Math.max(screen.width / 2 - 43, 170)), mouseX, mouseY);
            }
        }, 0);
        this.isWhitelist = isWhitelist;
    }

    @Override
    protected int getUOffset() {
        return isWhitelist ? 0 : 12;
    }

    public void setWhitelist(boolean whitelist) {
        this.isWhitelist = whitelist;
    }
}
