package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.utils.XpUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RemoveXpPacket {
    private final int xp;

    public RemoveXpPacket(int xp) {
        this.xp = xp;
    }

    public RemoveXpPacket(FriendlyByteBuf buffer) {
        this.xp = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.xp);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player == null) {
            return;
        }

        if (XpUtils.getPlayerTotalXp(player) >= xp) {
            player.giveExperiencePoints(-xp);
        }
    }
}
