package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.common.network.data.OpenBackpackPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public class OpenBackpackHandler {
    public static OpenBackpackHandler INSTANCE = new OpenBackpackHandler();

    public void handle(final OpenBackpackPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();

            if (player instanceof ServerPlayer serverPlayer) {
                List<ItemStack> backpacks = CrystalBackpack.findBackpackStacks(player);

                if (!backpacks.isEmpty()) {
                    ItemStack backpackStack = backpacks.getFirst();
                    if (backpackStack.is(Registration.CRYSTAL_BACKPACK.get())) {
                        CrystalBackpack backpackItem = (CrystalBackpack) backpackStack.getItem();
                        backpackItem.openBackpack(serverPlayer, backpackStack);
                    }
                }
            }
        });
    }
}
