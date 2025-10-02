package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen;
import dev.willyelton.crystal_tools.client.gui.CrystalFurnaceScreen;
import dev.willyelton.crystal_tools.client.gui.CrystalGeneratorScreen;
import dev.willyelton.crystal_tools.client.gui.CrystalPedestalScreen;
import dev.willyelton.crystal_tools.client.gui.CrystalQuarryScreen;
import dev.willyelton.crystal_tools.client.gui.FilterConfigScreen;
import dev.willyelton.crystal_tools.client.gui.PortableGeneratorScreen;
import dev.willyelton.crystal_tools.common.inventory.container.CrystalMagnetContainerMenu;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class RegisterMenuScreensEvent {
    @SubscribeEvent
    public static void onRegisterMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(ModRegistration.CRYSTAL_FURNACE_CONTAINER.get(), CrystalFurnaceScreen::new);
        event.register(ModRegistration.CRYSTAL_GENERATOR_CONTAINER.get(), CrystalGeneratorScreen::new);
        event.register(ModRegistration.PORTABLE_CRYSTAL_GENERATOR_CONTAINER.get(), PortableGeneratorScreen::new);
        event.register(ModRegistration.CRYSTAL_QUARRY_CONTAINER.get(), CrystalQuarryScreen::new);
        event.register(ModRegistration.CRYSTAL_BACKPACK_CONTAINER.get(), CrystalBackpackScreen::new);
        event.register(ModRegistration.CRYSTAL_PEDESTAL_CONTAINER.get(), CrystalPedestalScreen::new);

        // Little hacky, the generic type is backpack screen but that will always be null in this case. Also, can't be a lamba because the compiler is dumb
        event.register(ModRegistration.CRYSTAL_MAGNET_CONTAINER.get(), new MenuScreens.ScreenConstructor<CrystalMagnetContainerMenu, FilterConfigScreen<CrystalMagnetContainerMenu, CrystalBackpackScreen>>() {
            @Override
            public FilterConfigScreen<CrystalMagnetContainerMenu, CrystalBackpackScreen> create(CrystalMagnetContainerMenu menu, Inventory inventory, Component title) {
                return new FilterConfigScreen<CrystalMagnetContainerMenu, CrystalBackpackScreen>(menu, inventory, null, false);
            }
        });
    }
}
