package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

            Thread thread = Thread.currentThread();

            if (player != null) {
                ServerLevel serverLevel = (ServerLevel) player.level;
                BlockPos pos = msg.pos;

                BlockState blockState = serverLevel.getBlockState(pos);
                BlockEntity blockEntity = serverLevel.getBlockEntity(pos);

                System.out.println(blockState);
                System.out.println(blockEntity);

                if (blockEntity instanceof CrystalFurnaceBlockEntity crystalFurnaceBlockEntity) {
                    System.out.println("HERE????");
                }
            }
//            Level level = ctx.get().getSender().level;
//            // TODO: getting called twice on client and 0 times on server
//            System.out.println("In handler");
//            if (level != null) {
//                BlockEntity blockEntity = level.getBlockEntity(msg.pos);
//                System.out.println(level.isClientSide);
//
//                System.out.println("Level not null in handler");
//                System.out.println(msg.pos);
//                System.out.println(blockEntity);
//
//                if (blockEntity instanceof CrystalFurnaceBlockEntity crystalFurnaceBlockEntity) {
//                    crystalFurnaceBlockEntity.addToData(msg.key, msg.value);
//                    System.out.println("Adding to data: " + msg.key + " with value: " + msg.value);
//                }
//
//                if (msg.id != -1 && blockEntity != null) {
//                    if (blockEntity instanceof CrystalFurnaceBlockEntity crystalFurnaceBlockEntity) {
//                        crystalFurnaceBlockEntity.addToPoints(msg.id, 1);
//                        System.out.println("Adding to points: " + msg.id);
//                    }
//                }
//            }
        }
    }
}
