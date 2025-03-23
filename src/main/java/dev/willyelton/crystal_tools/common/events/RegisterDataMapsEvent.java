package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = CrystalTools.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegisterDataMapsEvent {
    @SubscribeEvent
    public static void onRegisterDataMaps(final RegisterDataMapTypesEvent event) {
        event.register(DataMaps.GENERATOR_GEMS);
        event.register(DataMaps.GENERATOR_METALS);
        event.register(DataMaps.MOB_SKULLS);
    }
}
