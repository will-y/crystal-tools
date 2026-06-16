package dev.willyelton.crystal.tools.common.events;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal.tools.common.tags.CrystalToolsTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class AnvilUpdateEvent {
    @SubscribeEvent
    public static void handleAnvilUpdate(net.neoforged.neoforge.event.AnvilUpdateEvent event) {
        if (event.getLeft().is(CrystalToolsTags.CRYSTAL_TOOL) &&
                !CrystalToolsConfig.ENCHANT_TOOLS.get()) {
            event.setCanceled(true);
        }
    }
}