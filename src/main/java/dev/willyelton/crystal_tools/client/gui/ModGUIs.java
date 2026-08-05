package dev.willyelton.crystal_tools.client.gui;

import dev.willyelton.crystal_tools.common.capability.Levelable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ModGUIs {
    public static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    public static void openEntityScreen(LivingEntity livingEntity, Player player, Levelable levelable) {
        openScreen(new EntityUpgradeScreen(livingEntity, player, levelable));
    }
}
