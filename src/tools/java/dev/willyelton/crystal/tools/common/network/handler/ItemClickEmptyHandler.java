package dev.willyelton.crystal.tools.common.network.handler;

import dev.willyelton.crystal.core.common.levelable.ItemClickHandler;
import dev.willyelton.crystal.tools.common.network.data.ItemClickEmptyPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ItemClickEmptyHandler {
    public static final ItemClickEmptyHandler INSTANCE = new ItemClickEmptyHandler();

    public void handle(final ItemClickEmptyPayload payload, final IPayloadContext context) {
        Player player = context.player();
        ItemStack stack = player.getItemInHand(payload.hand());
        if (stack.getItem() instanceof ItemClickHandler itemClickHandler) {
            itemClickHandler.leftClickAir(stack, player.level(), player, payload.shifting());
        }
    }
}
