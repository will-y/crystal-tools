package dev.willyelton.crystal.core.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ScreenUtils {
    private ScreenUtils() {}

    public static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }
}
