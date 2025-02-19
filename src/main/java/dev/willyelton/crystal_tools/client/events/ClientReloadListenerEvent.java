package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.renderer.CrystalShieldRenderer;
import dev.willyelton.crystal_tools.client.renderer.CrystalTridentBlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientReloadListenerEvent {
    @SubscribeEvent
    public static void handleReloadListener(RegisterClientReloadListenersEvent reloadListenerEvent) {
        reloadListenerEvent.registerReloadListener(CrystalTridentBlockEntityWithoutLevelRenderer.INSTANCE);
        reloadListenerEvent.registerReloadListener(CrystalShieldRenderer.INSTANCE);
    }
}
