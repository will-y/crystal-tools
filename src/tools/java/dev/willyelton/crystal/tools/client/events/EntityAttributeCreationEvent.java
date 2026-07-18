package dev.willyelton.crystal.tools.client.events;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.ModRegistration;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class EntityAttributeCreationEvent {
    @SubscribeEvent
    public static void createDefaultAttributes(net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event) {
        event.put(
                ModRegistration.CRYSTAL_GOLEM_ENTITY.get(),
                CopperGolem.createAttributes().build()
        );
    }
}
