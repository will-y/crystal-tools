package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.gui.CrystalBackpackScreen;
import dev.willyelton.crystal_tools.client.gui.CrystalFurnaceScreen;
import dev.willyelton.crystal_tools.client.gui.CrystalGeneratorScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class RegisterMenuScreensEvent {
    @SubscribeEvent
    public static void onRegisterMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(Registration.CRYSTAL_FURNACE_CONTAINER.get(), CrystalFurnaceScreen::new);
        event.register(Registration.CRYSTAL_GENERATOR_CONTAINER.get(), CrystalGeneratorScreen::new);
        event.register(Registration.CRYSTAL_BACKPACK_CONTAINER.get(), CrystalBackpackScreen::new);
    }
}
