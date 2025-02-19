package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ShieldBlockEvent {

    @SubscribeEvent
    public static void handleShieldEvent(LivingShieldBlockEvent event) {
        System.out.println(event);
    }
}
