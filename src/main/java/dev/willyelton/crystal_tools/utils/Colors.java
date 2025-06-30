package dev.willyelton.crystal_tools.utils;

public class Colors {
    public static int fromRGB(int red, int green, int blue) {
        return fromRGB(red, green, blue, 255);
    }

    public static int fromRGB(int red, int green, int blue, int alpha) {
        return blue + (green << 8) + (red << 16) + (alpha << 24);
    }

    public static int addAlpha(int color, int alpha) {
        int existingAlpha = color >> 24;
        if (existingAlpha != -1) return color;
        return color + (alpha << 24);
    }
}
