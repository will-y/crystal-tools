package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.gui.ScrollableMenu;
import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ScrollPacket {
    private final int row;

    public ScrollPacket(int row) {
        this.row = row;
    }

    public ScrollPacket(FriendlyByteBuf buffer) {
        this.row = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.row);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player == null) {
            return;
        }

        if (player.containerMenu instanceof ScrollableMenu menu) {
            menu.scrollTo(this.row);
        }
    }
}
