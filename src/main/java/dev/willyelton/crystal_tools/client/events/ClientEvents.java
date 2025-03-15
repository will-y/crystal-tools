package dev.willyelton.crystal_tools.client.events;


import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.renderer.CrystalTridentRenderer;
import dev.willyelton.crystal_tools.client.renderer.QuarryCubeRenderer;
import dev.willyelton.crystal_tools.client.renderer.blockentity.CrystalQuarryBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientEvents {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.CRYSTAL_TRIDENT_ENTITY.get(), CrystalTridentRenderer::new);
        event.registerBlockEntityRenderer(Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(), CrystalQuarryBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(QuarryCubeRenderer.LOCATION, QuarryCubeRenderer::createBodyLayer);
    }
}
