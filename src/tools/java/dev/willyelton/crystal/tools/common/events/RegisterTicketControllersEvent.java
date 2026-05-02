package dev.willyelton.crystal.tools.common.events;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.core.common.block.entity.action.ChunkLoadingAction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class RegisterTicketControllersEvent {
    @SubscribeEvent
    public static void registerTicketControllers(net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent event) {
        event.register(ChunkLoadingAction.TICKET_CONTROLLER);
    }
}
