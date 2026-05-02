package dev.willyelton.crystal.core.common.network;

import dev.willyelton.crystal.core.common.network.handler.BlockSkillHandler;
import dev.willyelton.crystal.core.common.network.handler.ContainerRowsHandler;
import dev.willyelton.crystal.core.common.network.handler.EntitySkillHandler;
import dev.willyelton.crystal.core.common.network.handler.OpenContainerHandler;
import dev.willyelton.crystal.core.common.network.handler.OpenSubScreenHandler;
import dev.willyelton.crystal.core.common.network.handler.PointsFromXpHandler;
import dev.willyelton.crystal.core.common.network.handler.RemoveItemHandler;
import dev.willyelton.crystal.core.common.network.handler.ResetSkillsBlockHandler;
import dev.willyelton.crystal.core.common.network.handler.ResetSkillsHandler;
import dev.willyelton.crystal.core.common.network.handler.ScrollHandler;
import dev.willyelton.crystal.core.common.network.handler.ToolHealHandler;
import dev.willyelton.crystal.core.common.network.handler.ToolSkillHandler;
import dev.willyelton.crystal.core.common.network.handler.UpdateSideConfigHandler;
import dev.willyelton.crystal.core.common.network.payload.BlockSkillPayload;
import dev.willyelton.crystal.core.common.network.payload.ContainerRowsPayload;
import dev.willyelton.crystal.core.common.network.payload.EntitySkillPayload;
import dev.willyelton.crystal.core.common.network.payload.OpenContainerPayload;
import dev.willyelton.crystal.core.common.network.payload.OpenSubScreenPayload;
import dev.willyelton.crystal.core.common.network.payload.PointsFromXpPayload;
import dev.willyelton.crystal.core.common.network.payload.RemoveItemPayload;
import dev.willyelton.crystal.core.common.network.payload.ResetSkillsBlockPayload;
import dev.willyelton.crystal.core.common.network.payload.ResetSkillsPayload;
import dev.willyelton.crystal.core.common.network.payload.ScrollPayload;
import dev.willyelton.crystal.core.common.network.payload.ToolHealPayload;
import dev.willyelton.crystal.core.common.network.payload.ToolSkillPayload;
import dev.willyelton.crystal.core.common.network.payload.UpdateSideConfigPayload;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class RegisterPackets {

    @SubscribeEvent
    public static void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1.0.0");

        registrar.playToServer(ToolSkillPayload.TYPE, ToolSkillPayload.STREAM_CODEC, ToolSkillHandler.INSTANCE::handle);
        registrar.playToServer(BlockSkillPayload.TYPE, BlockSkillPayload.STREAM_CODEC, BlockSkillHandler.INSTANCE::handle);
        registrar.playToServer(EntitySkillPayload.TYPE, EntitySkillPayload.STREAM_CODEC, EntitySkillHandler.INSTANCE::handle);
        registrar.playToServer(ResetSkillsPayload.TYPE, ResetSkillsPayload.STREAM_CODEC, ResetSkillsHandler.INSTANCE::handle);
        registrar.playToServer(ResetSkillsBlockPayload.TYPE, ResetSkillsBlockPayload.STREAM_CODEC, ResetSkillsBlockHandler.INSTANCE::handle);
        registrar.playToServer(ToolHealPayload.TYPE, ToolHealPayload.STREAM_CODEC, ToolHealHandler.INSTANCE::handle);
        registrar.playToServer(OpenContainerPayload.TYPE, OpenContainerPayload.STREAM_CODEC, OpenContainerHandler.INSTANCE::handle);
        registrar.playToServer(OpenSubScreenPayload.TYPE, OpenSubScreenPayload.STREAM_CODEC, OpenSubScreenHandler.INSTANCE::handle);
        registrar.playToServer(UpdateSideConfigPayload.TYPE, UpdateSideConfigPayload.STREAM_CODEC, UpdateSideConfigHandler.INSTANCE::handle);
        registrar.playToServer(RemoveItemPayload.TYPE, RemoveItemPayload.STREAM_CODEC, RemoveItemHandler.INSTANCE::handle);
        registrar.playToServer(PointsFromXpPayload.TYPE, PointsFromXpPayload.STREAM_CODEC, PointsFromXpHandler.INSTANCE::handle);
        registrar.playToServer(ScrollPayload.TYPE, ScrollPayload.STREAM_CODEC, ScrollHandler.INSTANCE::handle);
        registrar.playToServer(ContainerRowsPayload.TYPE, ContainerRowsPayload.STREAM_CODEC, ContainerRowsHandler.INSTANCE::handle);
    }
}
