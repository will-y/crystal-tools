package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.network.packet.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {
    private static short index = 0;

    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(CrystalTools.MODID, "messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    public static void register() {
        // Client to Server
        HANDLER.messageBuilder(BlockAttributePacket.class, index++)
                .decoder(BlockAttributePacket::new)
                .encoder(BlockAttributePacket::encode)
                .consumerMainThread(BlockAttributePacket::handle)
                .add();

        HANDLER.messageBuilder(ModeSwitchPacket.class, index++)
                .decoder(ModeSwitchPacket::new)
                .encoder(ModeSwitchPacket::encode)
                .consumerMainThread(ModeSwitchPacket::handle)
                .add();

        HANDLER.messageBuilder(RemoveItemPacket.class, index++)
                .decoder(RemoveItemPacket::new)
                .encoder(RemoveItemPacket::encode)
                .consumerMainThread(RemoveItemPacket::handle)
                .add();

        HANDLER.messageBuilder(ResetSkillsPacket.class, index++)
                .decoder(ResetSkillsPacket::new)
                .encoder(ResetSkillsPacket::encode)
                .consumerMainThread(ResetSkillsPacket::handle)
                .add();

        HANDLER.messageBuilder(ToolAttributePacket.class, index++)
                .decoder(ToolAttributePacket::new)
                .encoder(ToolAttributePacket::encode)
                .consumerMainThread(ToolAttributePacket::handle)
                .add();

        HANDLER.messageBuilder(ToolHealPacket.class, index++)
                .decoder(ToolHealPacket::new)
                .encoder(ToolHealPacket::encode)
                .consumerMainThread(ToolHealPacket::handle)
                .add();

        HANDLER.messageBuilder(BackpackScreenPacket.class, index++)
                .decoder(BackpackScreenPacket::new)
                .encoder(BackpackScreenPacket::encode)
                .consumerMainThread(BackpackScreenPacket::handle)
                .add();

        // Server to Client
        HANDLER.messageBuilder(SkillCacheUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SkillCacheUpdatePacket::new)
                .encoder(SkillCacheUpdatePacket::encode)
                .consumerMainThread(SkillCacheUpdatePacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        HANDLER.sendToServer(msg);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        HANDLER.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static  <MSG> void sendToAllPlayers(MSG message) {
        HANDLER.send(PacketDistributor.ALL.noArg(), message);
    }

}
