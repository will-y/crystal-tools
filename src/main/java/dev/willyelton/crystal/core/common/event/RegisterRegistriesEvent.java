package dev.willyelton.crystal.core.common.event;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class RegisterRegistriesEvent {
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(Registration.ACTION_TYPE_REGISTRY);
    }
}
