package dev.willyelton.crystal_tools.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.getHandSlots().forEach(itemStack -> {
                if (itemStack.getItem() instanceof LevelableItem) {
                    DataComponents.addToComponent(itemStack, DataComponents.SKILL_POINTS, points);
                }
            });
        }

        return 1;
    }
}
