package dev.willyelton.crystal.core.common.event;

import com.mojang.brigadier.CommandDispatcher;
import dev.willyelton.crystal.core.common.command.AddPointsBlockCommand;
import dev.willyelton.crystal.core.common.command.AddPointsCommand;
import dev.willyelton.crystal.core.common.command.AddPointsEntityCommand;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class RegisterCommandEvent {
    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();

        AddPointsCommand.register(commandDispatcher);
        AddPointsBlockCommand.register(commandDispatcher);
        AddPointsEntityCommand.register(commandDispatcher);
    }
}
