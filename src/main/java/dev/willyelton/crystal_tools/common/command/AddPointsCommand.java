package dev.willyelton.crystal_tools.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.server.permissions.Permissions.COMMANDS_MODERATOR;

public class AddPointsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> addPointsCommand =
                Commands.literal("add_points")
                        .requires((commandSourceStack -> commandSourceStack.permissions().hasPermission(COMMANDS_MODERATOR)))
                        .then(Commands.argument("points", IntegerArgumentType.integer(1))
                                .executes((commandContext) -> addPointsToTool(commandContext, IntegerArgumentType.getInteger(commandContext, "points"))));

        dispatcher.register(addPointsCommand);
    }

    static int addPointsToTool(CommandContext<CommandSourceStack> commandContext, int points) {
        Entity entity = commandContext.getSource().getEntity();

        if (entity instanceof LivingEntity livingEntity) {
            ItemStack stack = livingEntity.getItemInHand(InteractionHand.MAIN_HAND);
            Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, livingEntity.level().registryAccess());
            if (levelable != null) {
                DataComponents.addToComponent(stack, DataComponents.SKILL_POINTS, points);
            }
        }

        return 1;
    }
}
