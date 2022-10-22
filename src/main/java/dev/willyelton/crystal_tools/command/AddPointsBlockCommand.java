package dev.willyelton.crystal_tools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.willyelton.crystal_tools.levelable.block.entity.LevelableBlockEntity;
import dev.willyelton.crystal_tools.network.AddSkillPointsToClientPacket;
import dev.willyelton.crystal_tools.network.PacketHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AddPointsBlockCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> addPointsCommand =
                Commands.literal("add_points_block")
                        .requires((commandSourceStack -> commandSourceStack.hasPermission(2)))
                        .then(Commands.argument("points", IntegerArgumentType.integer(0))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes((commandContext) -> addPointsToBlock(commandContext,
                                                IntegerArgumentType.getInteger(commandContext, "points"),
                                                BlockPosArgument.getLoadedBlockPos(commandContext, "pos")))));

        dispatcher.register(addPointsCommand);
    }

    static int addPointsToBlock(CommandContext<CommandSourceStack> commandContext, int points, BlockPos pos) {
        // Server Thread
        Level level = commandContext.getSource().getLevel();

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof LevelableBlockEntity levelableBlockEntity) {
            levelableBlockEntity.addSkillPoints(points);
        }

        ServerPlayer player = commandContext.getSource().getPlayer();

        PacketHandler.sendToPlayer(new AddSkillPointsToClientPacket(pos, points), player);

        return 1;
    }
}
