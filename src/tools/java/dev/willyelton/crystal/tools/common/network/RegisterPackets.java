package dev.willyelton.crystal.tools.common.network;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.common.network.data.BackpackScreenPayload;
import dev.willyelton.crystal.tools.common.network.data.BlockBreakPayload;
import dev.willyelton.crystal.tools.common.network.data.BlockStripPayload;
import dev.willyelton.crystal.tools.common.network.data.ModeSwitchPayload;
import dev.willyelton.crystal.tools.common.network.data.OpenBackpackPayload;
import dev.willyelton.crystal.tools.common.network.data.QuarryMineBlockPayload;
import dev.willyelton.crystal.tools.common.network.data.TriggerRocketPayload;
import dev.willyelton.crystal.tools.common.network.data.VeinMiningPayload;
import dev.willyelton.crystal.tools.common.network.handler.BackpackScreenHandler;
import dev.willyelton.crystal.tools.common.network.handler.BlockBreakHandler;
import dev.willyelton.crystal.tools.common.network.handler.BlockStripHandler;
import dev.willyelton.crystal.tools.common.network.handler.ModeSwitchHandler;
import dev.willyelton.crystal.tools.common.network.handler.OpenBackpackHandler;
import dev.willyelton.crystal.tools.common.network.handler.QuarryMineBlockHandler;
import dev.willyelton.crystal.tools.common.network.handler.TriggerRocketHandler;
import dev.willyelton.crystal.tools.common.network.handler.VeinMiningHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class RegisterPackets {

    @SubscribeEvent
    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.1.0");

        // Client to Server
        registrar.playToServer(ModeSwitchPayload.TYPE, ModeSwitchPayload.STREAM_CODEC, ModeSwitchHandler.INSTANCE::handle);
        registrar.playToServer(BackpackScreenPayload.TYPE, BackpackScreenPayload.STREAM_CODEC, BackpackScreenHandler.INSTANCE::handle);
        registrar.playToServer(BlockBreakPayload.TYPE, BlockBreakPayload.STREAM_CODEC, BlockBreakHandler.INSTANCE::handle);
        registrar.playToServer(BlockStripPayload.TYPE, BlockStripPayload.STREAM_CODEC, BlockStripHandler.INSTANCE::handle);
        registrar.playToServer(OpenBackpackPayload.TYPE, OpenBackpackPayload.STREAM_CODEC, OpenBackpackHandler.INSTANCE::handle);
        registrar.playToServer(VeinMiningPayload.TYPE, VeinMiningPayload.STREAM_CODEC, VeinMiningHandler.INSTANCE::handle);
        registrar.playToServer(TriggerRocketPayload.TYPE, TriggerRocketPayload.STREAM_CODEC, TriggerRocketHandler.INSTANCE::handle);

        // Server to Client
        // If any client handler requires client only classes it will have to go in to the new client only even
        registrar.playToClient(QuarryMineBlockPayload.TYPE, QuarryMineBlockPayload.STREAM_CODEC, QuarryMineBlockHandler.INSTANCE::handle);
    }
}
