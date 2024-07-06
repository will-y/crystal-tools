package dev.willyelton.crystal_tools.common.network.handler;

import dev.willyelton.crystal_tools.common.compat.curios.CuriosCompatibility;
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
                int slot = payload.slotId() == -1 ? CrystalBackpack.findNextBackpackSlot(player) : payload.slotId();
                if (slot == -1) return;

                ItemStack backpackStack;

                if (slot == -2) {
                    List<ItemStack> curiosStacks = CuriosCompatibility.getCrystalBackpacksInCurios(player);

                    if (!curiosStacks.isEmpty()) {
                        backpackStack = curiosStacks.getFirst();
                    } else {
                        backpackStack = ItemStack.EMPTY;
                    }
                } else {
                    backpackStack = player.getInventory().getItem(slot);
                }

                if (backpackStack.getItem() instanceof CrystalBackpack backpackItem) {
                    backpackItem.openBackpack(serverPlayer, backpackStack, slot);
                }
            }
        });
    }
}
