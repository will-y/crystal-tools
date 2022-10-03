package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class BlockAttributePacket {
    private final String key;
    private final float value;
    private final int id;
    private final BlockPos pos;

    public BlockAttributePacket(String key, float value, int id, BlockPos pos) {
        this.key = key;
        this.value = value;
        this.id = id;
        this.pos = pos;
    }

    public static void encode(BlockAttributePacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.key.length());
        buffer.writeCharSequence(msg.key, Charset.defaultCharset());
        buffer.writeFloat(msg.value);
        buffer.writeInt(msg.id);
        buffer.writeBlockPos(msg.pos);
    }

    public static BlockAttributePacket decode(FriendlyByteBuf buffer) {
        int keyLen = buffer.readInt();
        String key = buffer.readCharSequence(keyLen, Charset.defaultCharset()).toString();
        return new BlockAttributePacket(key, buffer.readFloat(), buffer.readInt(), buffer.readBlockPos());
    }

    public static class Handler {
        public static void handle(final BlockAttributePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Level level = player.level;
                BlockEntity blockEntity = level.getBlockEntity(msg.pos);
                boolean needChange = false;

                if (blockEntity instanceof CrystalFurnaceBlockEntity) {
                    NBTUtils.addValueToTag(blockEntity.getPersistentData(), msg.key, msg.value);
                    needChange = true;
                }

                if (msg.id != -1 && blockEntity != null) {
                    NBTUtils.addValueToArray(blockEntity.getPersistentData(), "points", msg.id, 1);
                    needChange = true;
                }

                if (needChange) {
                    blockEntity.setChanged();
                }
            }
        }
    }
}
