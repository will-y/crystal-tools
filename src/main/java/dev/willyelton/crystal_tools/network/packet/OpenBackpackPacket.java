package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class OpenBackpackPacket {
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        List<ItemStack> backpacks = CrystalBackpack.findBackpackStacks(player);

        if (!backpacks.isEmpty()) {
            ItemStack backpackStack = backpacks.get(0);
            if (backpackStack.is(Registration.CRYSTAL_BACKPACK.get())) {
                CrystalBackpack backpackItem = (CrystalBackpack) backpackStack.getItem();
                backpackItem.openBackpack(player, backpackStack);
            }
        }
    }
}
