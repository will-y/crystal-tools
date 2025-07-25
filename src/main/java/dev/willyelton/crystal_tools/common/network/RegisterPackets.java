package dev.willyelton.crystal_tools.common.network;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.network.data.BackpackScreenPayload;
import dev.willyelton.crystal_tools.common.network.data.BlockBreakPayload;
import dev.willyelton.crystal_tools.common.network.data.BlockSkillPayload;
import dev.willyelton.crystal_tools.common.network.data.BlockStripPayload;
import dev.willyelton.crystal_tools.common.network.data.ContainerRowsPayload;
import dev.willyelton.crystal_tools.common.network.data.ModeSwitchPayload;
import dev.willyelton.crystal_tools.common.network.data.OpenBackpackPayload;
import dev.willyelton.crystal_tools.common.network.data.OpenContainerPayload;
import dev.willyelton.crystal_tools.common.network.data.PointsFromXpPayload;
import dev.willyelton.crystal_tools.common.network.data.QuarryMineBlockPayload;
import dev.willyelton.crystal_tools.common.network.data.RemoveItemPayload;
import dev.willyelton.crystal_tools.common.network.data.ResetSkillsBlockPayload;
import dev.willyelton.crystal_tools.common.network.data.ResetSkillsPayload;
import dev.willyelton.crystal_tools.common.network.data.ScrollPayload;
import dev.willyelton.crystal_tools.common.network.data.ToolHealPayload;
import dev.willyelton.crystal_tools.common.network.data.ToolSkillPayload;
import dev.willyelton.crystal_tools.common.network.data.TriggerRocketPayload;
import dev.willyelton.crystal_tools.common.network.data.VeinMiningPayload;
import dev.willyelton.crystal_tools.common.network.handler.BackpackScreenHandler;
import dev.willyelton.crystal_tools.common.network.handler.BlockBreakHandler;
import dev.willyelton.crystal_tools.common.network.handler.BlockSkillHandler;
import dev.willyelton.crystal_tools.common.network.handler.BlockStripHandler;
import dev.willyelton.crystal_tools.common.network.handler.ContainerRowsHandler;
import dev.willyelton.crystal_tools.common.network.handler.ModeSwitchHandler;
import dev.willyelton.crystal_tools.common.network.handler.OpenBackpackHandler;
import dev.willyelton.crystal_tools.common.network.handler.OpenContainerHandler;
import dev.willyelton.crystal_tools.common.network.handler.PointsFromXpHandler;
import dev.willyelton.crystal_tools.common.network.handler.QuarryMineBlockHandler;
import dev.willyelton.crystal_tools.common.network.handler.RemoveItemHandler;
import dev.willyelton.crystal_tools.common.network.handler.ResetSkillsBlockHandler;
import dev.willyelton.crystal_tools.common.network.handler.ResetSkillsHandler;
import dev.willyelton.crystal_tools.common.network.handler.ScrollHandler;
import dev.willyelton.crystal_tools.common.network.handler.ToolHealHandler;
import dev.willyelton.crystal_tools.common.network.handler.ToolSkillHandler;
import dev.willyelton.crystal_tools.common.network.handler.TriggerRocketHandler;
import dev.willyelton.crystal_tools.common.network.handler.VeinMiningHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class RegisterPackets {

    @SubscribeEvent
    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");

        // Client to Server
        registrar.playToServer(ToolSkillPayload.TYPE, ToolSkillPayload.STREAM_CODEC, ToolSkillHandler.INSTANCE::handle);
        registrar.playToServer(BlockSkillPayload.TYPE, BlockSkillPayload.STREAM_CODEC, BlockSkillHandler.INSTANCE::handle);
        registrar.playToServer(ModeSwitchPayload.TYPE, ModeSwitchPayload.STREAM_CODEC, ModeSwitchHandler.INSTANCE::handle);
        registrar.playToServer(RemoveItemPayload.TYPE, RemoveItemPayload.STREAM_CODEC, RemoveItemHandler.INSTANCE::handle);
        registrar.playToServer(ResetSkillsPayload.TYPE, ResetSkillsPayload.STREAM_CODEC, ResetSkillsHandler.INSTANCE::handle);
        registrar.playToServer(ToolHealPayload.TYPE, ToolHealPayload.STREAM_CODEC, ToolHealHandler.INSTANCE::handle);
        registrar.playToServer(BackpackScreenPayload.TYPE, BackpackScreenPayload.STREAM_CODEC, BackpackScreenHandler.INSTANCE::handle);
        registrar.playToServer(ScrollPayload.TYPE, ScrollPayload.STREAM_CODEC, ScrollHandler.INSTANCE::handle);
        registrar.playToServer(ContainerRowsPayload.TYPE, ContainerRowsPayload.STREAM_CODEC, ContainerRowsHandler.INSTANCE::handle);
        registrar.playToServer(BlockBreakPayload.TYPE, BlockBreakPayload.STREAM_CODEC, BlockBreakHandler.INSTANCE::handle);
        registrar.playToServer(BlockStripPayload.TYPE, BlockStripPayload.STREAM_CODEC, BlockStripHandler.INSTANCE::handle);
        registrar.playToServer(OpenBackpackPayload.TYPE, OpenBackpackPayload.STREAM_CODEC, OpenBackpackHandler.INSTANCE::handle);
        registrar.playToServer(VeinMiningPayload.TYPE, VeinMiningPayload.STREAM_CODEC, VeinMiningHandler.INSTANCE::handle);
        registrar.playToServer(ResetSkillsBlockPayload.TYPE, ResetSkillsBlockPayload.STREAM_CODEC, ResetSkillsBlockHandler.INSTANCE::handle);
        registrar.playToServer(OpenContainerPayload.TYPE, OpenContainerPayload.STREAM_CODEC, OpenContainerHandler.INSTANCE::handle);
        registrar.playToServer(TriggerRocketPayload.TYPE, TriggerRocketPayload.STREAM_CODEC, TriggerRocketHandler.INSTANCE::handle);
        registrar.playToServer(PointsFromXpPayload.TYPE, PointsFromXpPayload.STREAM_CODEC, PointsFromXpHandler.INSTANCE::handle);

        // Server to Client
        // If any client handler requires client only classes it will have to go in to the new client only even
        registrar.playToClient(QuarryMineBlockPayload.TYPE, QuarryMineBlockPayload.STREAM_CODEC, QuarryMineBlockHandler.INSTANCE::handle);
    }
}
