package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModGUIs {
    // Might want to do more here later.
    public static boolean openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
        return false;
    }

    public static void initScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.CRYSTAL_FURNACE_CONTAINER.get(), CrystalFurnaceScreen::new);
        });
    }
}
