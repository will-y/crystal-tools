package dev.willyelton.crystal_tools.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.capability.LevelableEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import static net.minecraft.server.permissions.Permissions.COMMANDS_MODERATOR;

public class AddPointsEntityCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> addPointsCommand =
                Commands.literal("add_points_entity")
                        .requires((commandSourceStack -> commandSourceStack.permissions().hasPermission(COMMANDS_MODERATOR)))
                        .then(Commands.argument("points", IntegerArgumentType.integer(1))
                                .then(Commands.argument("entity", EntityArgument.entity())
                                        .executes((context -> addPointsToEntity(
                                                context,
                                                IntegerArgumentType.getInteger(context, "points"),
                                                EntityArgument.getEntity(context, "entity"))))));

        dispatcher.register(addPointsCommand);
    }

    static int addPointsToEntity(CommandContext<CommandSourceStack> commandContext, int points, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            Levelable levelable = LevelableEntity.of(livingEntity, null);

            if (levelable != null) {
                livingEntity.getData(ModRegistration.ENTITY_SKILL).addSkillPoints(points);
                livingEntity.syncData(ModRegistration.ENTITY_SKILL);
            }
        }

        return 1;
    }
}
