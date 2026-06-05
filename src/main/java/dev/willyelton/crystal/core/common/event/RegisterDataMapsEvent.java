package dev.willyelton.crystal.core.common.event;

import dev.willyelton.crystal.core.common.datamap.CrystalCoreDataMaps;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class RegisterDataMapsEvent {
    @SubscribeEvent
    public static void onRegisterDataMaps(final RegisterDataMapTypesEvent event) {
        // TODO: Move to core
        event.register(CrystalCoreDataMaps.MOB_HEADS);
        event.register(CrystalCoreDataMaps.SKILL_TREES);
        event.register(CrystalCoreDataMaps.ENTITY_SKILL_TREES);
    }
}
