package dev.willyelton.crystal_tools.event.client;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.renderer.CrystalTridentBlockEntityWithoutLevelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class ClientReloadListenerEvent {
    @SubscribeEvent
    public static void handleReloadListener(AddReloadListenerEvent reloadListenerEvent) {
        reloadListenerEvent.addListener(CrystalTridentBlockEntityWithoutLevelRenderer.INSTANCE);
    }
}
