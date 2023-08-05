package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToolHealPacket {

    public ToolHealPacket() {}

    public ToolHealPacket(FriendlyByteBuf buffer) {}

    public void encode(FriendlyByteBuf buffer) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer playerEntity = ctx.get().getSender();
        if (playerEntity != null) {
            ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(playerEntity);

            if (!heldTool.isEmpty()) {
                heldTool.setDamageValue(0);
                NBTUtils.addValueToTag(heldTool, "skill_points", -1);
            }
        }
    }
}
