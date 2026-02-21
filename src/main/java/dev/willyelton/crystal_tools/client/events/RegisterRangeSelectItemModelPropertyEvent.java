package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.renderer.item.properties.BowUseDuration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class RegisterRangeSelectItemModelPropertyEvent {
    @SubscribeEvent
    public static void registerRangeSelectItemModelPropertyEvent(net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(rl("bow_use_duration"), BowUseDuration.MAP_CODEC);
    }
}
