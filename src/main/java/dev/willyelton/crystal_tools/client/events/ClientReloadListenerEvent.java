package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.renderer.CrystalTridentBlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class ClientReloadListenerEvent {
    @SubscribeEvent
    public static void handleReloadListener(AddReloadListenerEvent reloadListenerEvent) {
        reloadListenerEvent.addListener(CrystalTridentBlockEntityWithoutLevelRenderer.INSTANCE);
    }
}
