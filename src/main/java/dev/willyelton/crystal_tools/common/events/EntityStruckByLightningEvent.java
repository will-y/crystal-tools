package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.entity.CrystalTridentEntity;
import net.minecraft.world.entity.LightningBolt;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class EntityStruckByLightningEvent {
    @SubscribeEvent
    public static void handleEntityStruckByLightningEvent(net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent event) {
        LightningBolt lightningBolt = event.getLightning();
        if (lightningBolt.getCause() != null && lightningBolt.getCause().equals(event.getEntity()) &&
                lightningBolt.getTags().contains(CrystalTridentEntity.CRYSTAL_TOOLS_TRIDENT_LIGHTNING_TAG)) {
            event.setCanceled(true);
        }
    }
}
