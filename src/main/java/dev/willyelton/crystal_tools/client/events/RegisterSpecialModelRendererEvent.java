package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.renderer.CrystalShieldRenderer;
import dev.willyelton.crystal_tools.client.renderer.CrystalTridentSpecialRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class RegisterSpecialModelRendererEvent {

    @SubscribeEvent
    public static void onRegisterSpecialModelRenderer(net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent event) {
        event.register(rl("crystal_trident"), CrystalTridentSpecialRenderer.Unbaked.MAP_CODEC);
        event.register(rl("crystal_shield"), CrystalShieldRenderer.Unbaked.MAP_CODEC);
    }
}
