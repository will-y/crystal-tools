package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
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
            ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(playerEntity);

            if (!heldTool.isEmpty()) {
                heldTool.setDamageValue(0);
                NBTUtils.addValueToTag(heldTool, "skill_points", -1);
            }
        }
    }
}
