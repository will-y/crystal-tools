package dev.willyelton.crystal.core.common.capability;

import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class RegisterCapabilitiesEvent {
    @SubscribeEvent
    public static void onRegisterCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        // All levelable items
        for (Item item : BuiltInRegistries.ITEM) {
            event.registerItem(Capabilities.ITEM_SKILL,
                    LevelableStack::of,
                    item);
        }
    }
}
