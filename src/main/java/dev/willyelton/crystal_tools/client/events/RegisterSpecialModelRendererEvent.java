package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.renderer.CrystalTridentSpecialRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RegisterSpecialModelRendererEvent {

    @SubscribeEvent
    public static void onRegisterSpecialModelRenderer(net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "crystal_trident"), CrystalTridentSpecialRenderer.Unbaked.MAP_CODEC);
    }
}
