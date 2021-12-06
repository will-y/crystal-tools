package dev.willyelton.crystal_tools.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ModGUIs {
    // Might want to do more here later.
    public static boolean openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
        return false;
    }
}
