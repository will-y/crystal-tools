package dev.willyelton.crystal_tools.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ModGUIs {
    // TODO: This should probably move somewhere else
    public static boolean openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
        return false;
    }
}
