package dev.willyelton.crystal.tools.client.events;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.client.renderer.BlockOverlayRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class RegisterFeatureRenderersEvent {

    @SubscribeEvent
    public static void onFeatureRenderersEvent(net.neoforged.neoforge.client.event.RegisterFeatureRenderersEvent event) {
        event.register(BlockOverlayRenderer.TYPE, new BlockOverlayRenderer());
    }
}
