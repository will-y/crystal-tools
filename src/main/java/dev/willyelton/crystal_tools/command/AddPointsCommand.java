package dev.willyelton.crystal_tools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.willyelton.crystal_tools.tool.LevelableItem;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;

public class AddPointsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> addPointsCommand =
                Commands.literal("add_points")
                        .requires((commandSourceStack -> commandSourceStack.hasPermission(2)))
                        .then(Commands.argument("points", IntegerArgumentType.integer(0))
                                .executes((commandContext) -> addPointsToTool(commandContext, IntegerArgumentType.getInteger(commandContext, "points"))));

        dispatcher.register(addPointsCommand);
    }

    static int addPointsToTool(CommandContext<CommandSourceStack> commandContext, int points) throws CommandSyntaxException {
        Entity entity = commandContext.getSource().getEntity();

        if (entity != null) {
            entity.getHandSlots().forEach(itemStack -> {
                if (itemStack.getItem() instanceof LevelableItem) {
                    NBTUtils.addValueToTag(itemStack, "skill_points", points);
                }
            });
        }

        return 1;
    }
}
