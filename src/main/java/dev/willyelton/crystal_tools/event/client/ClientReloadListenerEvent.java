package dev.willyelton.crystal_tools.event.client;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.renderer.CrystalTridentBlockEntityWithoutLevelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientReloadListenerEvent {
    @SubscribeEvent
    public static void handleReloadListener(RegisterClientReloadListenersEvent reloadListenerEvent) {
        reloadListenerEvent.registerReloadListener(CrystalTridentBlockEntityWithoutLevelRenderer.INSTANCE);
    }
}
