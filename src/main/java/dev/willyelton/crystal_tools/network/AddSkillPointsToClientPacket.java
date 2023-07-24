package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.levelable.block.entity.LevelableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AddSkillPointsToClientPacket {
    private final BlockPos pos;
    private final int points;

    public AddSkillPointsToClientPacket(BlockPos pos, int points) {
        this.pos = pos;
        this.points = points;
    }

    public static void encode(AddSkillPointsToClientPacket msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.pos);
        buffer.writeInt(msg.points);
    }

    public static AddSkillPointsToClientPacket decode(FriendlyByteBuf buffer) {
        return new AddSkillPointsToClientPacket(buffer.readBlockPos(), buffer.readInt());
    }

    public static class Handler {
        public static void handle(final AddSkillPointsToClientPacket msg, Supplier<NetworkEvent.Context> ctx) {
            BlockPos pos = msg.pos;
            int points = msg.points;

            Player player = ctx.get().getSender();

            if (player != null) {
                Level level = player.level();
                BlockEntity blockEntity = level.getBlockEntity(pos);

                if (blockEntity instanceof LevelableBlockEntity levelableBlockEntity) {
                    levelableBlockEntity.addSkillPoints(points);
                }
            }
        }
    }
}
