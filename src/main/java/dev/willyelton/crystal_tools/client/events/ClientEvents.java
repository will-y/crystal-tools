package dev.willyelton.crystal_tools.client.events;


import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.renderer.CrystalToolsRenderTypes;
import dev.willyelton.crystal_tools.client.renderer.CrystalTridentRenderer;
import dev.willyelton.crystal_tools.client.renderer.QuarryCubeRenderer;
import dev.willyelton.crystal_tools.client.renderer.blockentity.CrystalPedestalBlockEntityRenderer;
import dev.willyelton.crystal_tools.client.renderer.blockentity.CrystalQuarryBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registration.CRYSTAL_TRIDENT_ENTITY.get(), CrystalTridentRenderer::new);
        event.registerBlockEntityRenderer(Registration.CRYSTAL_QUARRY_BLOCK_ENTITY.get(), CrystalQuarryBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(Registration.CRYSTAL_PEDESTAL_BLOCK_ENTITY.get(), CrystalPedestalBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(QuarryCubeRenderer.LOCATION, QuarryCubeRenderer::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(CrystalToolsRenderTypes.POSITION_COLOR_PIPELINE);
        event.registerPipeline(CrystalToolsRenderTypes.QUARRY_CUBE_PIPELINE);
    }
}
