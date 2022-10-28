package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ResetSkillsPacket {
    public static void encode(ResetSkillsPacket msg, FriendlyByteBuf buffer) {

    }

    public static ResetSkillsPacket decode(FriendlyByteBuf buffer) {
        return new ResetSkillsPacket();
    }

    public static void handle(final ResetSkillsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player != null) {
            ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(player);

            if (!heldTool.isEmpty()) {
                ToolUtils.resetPoints(heldTool);
            }
        }
    }
}
