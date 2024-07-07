package dev.willyelton.crystal_tools.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ModGUIs {
    public static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }
}
