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

    public RemoveItemPacket(FriendlyByteBuf buffer) {
        this.item = buffer.readItem();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeItem(this.item);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) return;

        ItemStack itemToRemove = this.item;

        if (itemToRemove.isEmpty()) return;

        Inventory inventory = player.getInventory();
        int index = inventory.findSlotMatchingItem(itemToRemove);

        if (index == -1) return;

        inventory.removeItem(index, 1);
    }
}
