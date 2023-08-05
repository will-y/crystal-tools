package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.levelable.block.container.CrystalFurnaceContainer;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class BlockAttributePacket {
    private final String key;
    private final float value;
    private final int id;

    public BlockAttributePacket(String key, float value, int id) {
        this.key = key;
        this.value = value;
        this.id = id;
    }

    public static void encode(BlockAttributePacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.key.length());
        buffer.writeCharSequence(msg.key, Charset.defaultCharset());
        buffer.writeFloat(msg.value);
        buffer.writeInt(msg.id);
    }

    public static BlockAttributePacket decode(FriendlyByteBuf buffer) {
        int keyLen = buffer.readInt();
        String key = buffer.readCharSequence(keyLen, Charset.defaultCharset()).toString();
        return new BlockAttributePacket(key, buffer.readFloat(), buffer.readInt());
    }

    public static class Handler {
        public static void handle(final BlockAttributePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer player = ctx.get().getSender();

            if (player != null) {
                AbstractContainerMenu container = player.containerMenu;

                if (container instanceof CrystalFurnaceContainer crystalFurnaceContainer) {
                    CrystalFurnaceBlockEntity blockEntity = crystalFurnaceContainer.getBlockEntity();

                    blockEntity.addToData(msg.key, msg.value);
                    if (msg.id != -1) {
                        blockEntity.addToPoints(msg.id, 1);
                    }
                }
            }
        }
    }
}
