package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ModeSwitchPacket {
    public static void encode(ModeSwitchPacket msg, FriendlyByteBuf buffer) {

    }

    public static ModeSwitchPacket decode(FriendlyByteBuf buffer) {
        return new ModeSwitchPacket();
    }

    public static class Handler {
        public static void handle(final ModeSwitchPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerEntity = ctx.get().getSender();
            if (playerEntity == null) return;

            ItemStack tool = ItemStackUtils.getHeldLevelableTool(playerEntity);
            if (tool.isEmpty()) return;
        }
    }
}
