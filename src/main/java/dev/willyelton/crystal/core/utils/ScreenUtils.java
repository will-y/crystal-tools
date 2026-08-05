package dev.willyelton.crystal.core.utils;

import dev.willyelton.crystal.core.client.gui.EntityUpgradeScreen;
import dev.willyelton.crystal.core.common.capability.Levelable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ScreenUtils {
    private ScreenUtils() {}

    public static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreenAndShow(screen);
    }

    public static void openEntityScreen(LivingEntity entity, Player player, Levelable levelable) {
        openScreen(new EntityUpgradeScreen(entity, player, levelable));
    }
}
