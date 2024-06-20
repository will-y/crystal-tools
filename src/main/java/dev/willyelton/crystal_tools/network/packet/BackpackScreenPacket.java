package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.inventory.container.CrystalBackpackContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BackpackScreenPacket {
    public enum Type {
        PICKUP_WHITELIST, PICKUP_BLACKLIST, SORT, COMPRESS, OPEN_COMPRESSION, OPEN_FILTER, CLOSE_SUB_SCREEN, REOPEN_BACKPACK
    }

    private final Type type;

    public BackpackScreenPacket(Type type) {
        this.type = type;
    }

    public BackpackScreenPacket(FriendlyByteBuf buffer) {
        type = Type.values()[buffer.readInt()];
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.type.ordinal());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player == null) {
            return;
        }

        if (player.containerMenu instanceof CrystalBackpackContainerMenu menu) {
            switch (type) {
                case PICKUP_WHITELIST -> menu.setWhitelist(true);
                case PICKUP_BLACKLIST -> menu.setWhitelist(false);
                case SORT -> menu.sort();
                case COMPRESS -> menu.compress();
                case OPEN_COMPRESSION -> menu.openCompressionScreen();
                case OPEN_FILTER -> menu.openFilterScreen();
                case CLOSE_SUB_SCREEN -> menu.closeSubScreen();
                case REOPEN_BACKPACK -> menu.reopenBackpack();
            }
        }
    }
}
