package dev.willyelton.crystal_tools.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.willyelton.crystal_tools.common.levelable.block.entity.LevelableBlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import static net.minecraft.server.permissions.Permissions.COMMANDS_MODERATOR;

public class AddPointsBlockCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> addPointsCommand =
                Commands.literal("add_points_block")
                        .requires((commandSourceStack -> commandSourceStack.permissions().hasPermission(COMMANDS_MODERATOR)))
                        .then(Commands.argument("points", IntegerArgumentType.integer(1))
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

        return 1;
    }
}
