package dev.willyelton.crystal_tools.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RemoveItemPacket {
    private final ItemStack item;

    public RemoveItemPacket(ItemStack item) {
        this.item = item;
    }

    public static void encode(RemoveItemPacket msg, FriendlyByteBuf buffer) {
        buffer.writeItem(msg.item);
    }

    public static RemoveItemPacket decode(FriendlyByteBuf buffer) {
        return new RemoveItemPacket(buffer.readItem());
    }

    public static class Handler {
        public static void handle(final RemoveItemPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack itemToRemove = msg.item;

            if (itemToRemove.isEmpty()) return;

            Inventory inventory = player.getInventory();
            int index = inventory.findSlotMatchingItem(itemToRemove);

            if (index == -1) return;

            inventory.removeItem(index, 1);
        }
    }
}
