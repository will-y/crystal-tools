package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.gui.ScrollableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ContainerRowsPacket {
    private final int rows;

    public ContainerRowsPacket(int rows) {
        this.rows = rows;
    }

    public ContainerRowsPacket(FriendlyByteBuf buffer) {
        this.rows = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.rows);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player == null) {
            return;
        }

        if (player.containerMenu instanceof ScrollableMenu menu) {
            menu.setMaxRows(this.rows);
        }
    }
}
