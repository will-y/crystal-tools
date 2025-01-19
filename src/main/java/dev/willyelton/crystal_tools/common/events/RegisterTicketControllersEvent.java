package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.block.entity.action.ChunkLoadingAction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegisterTicketControllersEvent {
    @SubscribeEvent
    public static void registerTicketControllers(net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent event) {
        event.register(ChunkLoadingAction.TICKET_CONTROLLER);
    }
}
