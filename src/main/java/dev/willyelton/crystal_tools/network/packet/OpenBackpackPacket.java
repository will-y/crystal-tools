package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.compat.curios.CuriosCompatibility;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class OpenBackpackPacket {
    private final int slotId;

    public OpenBackpackPacket() {
        this(-1);
    }

    public OpenBackpackPacket(int slotId) {
        this.slotId = slotId;
    }

    public OpenBackpackPacket(FriendlyByteBuf buffer) {
        this.slotId = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(slotId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player == null) return;

        int slot = slotId == -1 ? CrystalBackpack.findNextBackpackSlot(player) : slotId;
        if (slot == -1) return;

        ItemStack backpackStack;

        if (slot == -2) {
            List<ItemStack> curiosStacks = CuriosCompatibility.getCrystalBackpacksInCurios(player);

            if (!curiosStacks.isEmpty()) {
                backpackStack = curiosStacks.get(0);
            } else {
                backpackStack = ItemStack.EMPTY;
            }
        } else {
            backpackStack = player.getInventory().getItem(slot);
        }

        if (backpackStack.getItem() instanceof CrystalBackpack backpackItem) {
            backpackItem.openBackpack(player, backpackStack, slot);
        }
    }
}
