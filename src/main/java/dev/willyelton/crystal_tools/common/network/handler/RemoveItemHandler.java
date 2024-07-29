package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.network.data.RemoveItemPayload;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RemoveItemHandler {
    public static final RemoveItemHandler INSTANCE = new RemoveItemHandler();

    public void handle(final RemoveItemPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack itemToRemove = payload.stack();

            if (itemToRemove.isEmpty()) return;

            Inventory inventory = player.getInventory();
            int index = inventory.findSlotMatchingItem(itemToRemove);

            if (index == -1) return;

            inventory.removeItem(index, 1);
        });
    }
}