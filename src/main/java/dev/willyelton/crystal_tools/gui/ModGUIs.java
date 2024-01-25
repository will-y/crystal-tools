package dev.willyelton.crystal_tools.gui;

import dev.willyelton.crystal_tools.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ModGUIs {
    // Might want to do more here later.
    public static boolean openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
        return false;
    }

    public static void initScreens(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.CRYSTAL_FURNACE_CONTAINER.get(), CrystalFurnaceScreen::new);
            MenuScreens.register(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), CrystalBackpackScreen::new);
        });
    }
}
