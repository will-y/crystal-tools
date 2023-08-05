package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToolHealPacket {

    public static void encode(ToolHealPacket msg, FriendlyByteBuf buffer) {
    }

    public static ToolHealPacket decode(FriendlyByteBuf buffer) {
        return new ToolHealPacket();
    }

    public static class Handler {
        public static void handle(final ToolHealPacket msg, Supplier<NetworkEvent.Context> ctx) {
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
}
