package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.levelable.block.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModGUIs {
    // Might want to do more here later.
    public static boolean openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
        return false;
    }

    public static void initScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModBlocks.CRYSTAL_FURNACE_CONTAINER.get(), CrystalFurnaceScreen::new);
        });
    }
}
