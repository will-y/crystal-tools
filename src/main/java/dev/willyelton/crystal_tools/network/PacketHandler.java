package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.CrystalTools;
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
    private static final String PROTOCOL_VERSION = Integer.toString(4);
    private static short index = 0;
    // No idea what this does, probably don't need it
//    private static final PacketSplitManager SPLIT_MANAGER = new PacketSplitManager();

    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(CrystalTools.MODID, "messages"))
            .networkProtocolVersion(() -> "1.0")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();

    public static void register() {
        registerMessage(ToolAttributePacket.class, ToolAttributePacket::encode, ToolAttributePacket::decode, ToolAttributePacket.Handler::handle);
        registerMessage(ToolHealPacket.class, ToolHealPacket::encode, ToolHealPacket::decode, ToolHealPacket.Handler::handle);
        registerMessage(ModeSwitchPacket.class, ModeSwitchPacket::encode, ModeSwitchPacket::decode, ModeSwitchPacket.Handler::handle);
        registerMessage(RemoveItemPacket.class, RemoveItemPacket::encode, RemoveItemPacket::decode, RemoveItemPacket.Handler::handle);
        registerMessage(BlockAttributePacket.class, BlockAttributePacket::encode, BlockAttributePacket::decode, BlockAttributePacket.Handler::handle);

        registerMessageToClient(AddSkillPointsToClientPacket.class, AddSkillPointsToClientPacket::encode, AddSkillPointsToClientPacket::decode, AddSkillPointsToClientPacket.Handler::handle);
        registerMessage(ResetSkillsPacket.class, ResetSkillsPacket::encode, ResetSkillsPacket::decode, ResetSkillsPacket::handle);
    }

    public static void sendToServer(Object msg) {
        HANDLER.sendToServer(msg);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        HANDLER.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    private static <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        HANDLER.registerMessage(index, messageType, encoder, decoder, messageConsumer);
        index++;
        if (index > 0xFF)
            throw new RuntimeException("Too many messages!");
    }

    private static <MSG> void registerMessageToClient(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        HANDLER.registerMessage(index, messageType, encoder, decoder, messageConsumer, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        index++;
        if (index > 0xFF)
            throw new RuntimeException("Too many messages!");
    }
}
