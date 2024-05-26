package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.entity.CrystalTridentEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID)
public class EntityStruckByLightningEvent {
    @SubscribeEvent
    public static void handleEntityStruckByLightningEvent(net.minecraftforge.event.entity.EntityStruckByLightningEvent event) {
        LightningBolt lightningBolt = event.getLightning();
        if (lightningBolt.getCause() != null && lightningBolt.getCause().equals(event.getEntity()) &&
                lightningBolt.getTags().contains(CrystalTridentEntity.CRYSTAL_TOOLS_TRIDENT_LIGHTNING_TAG)) {
            event.setCanceled(true);
        }
    }
}
