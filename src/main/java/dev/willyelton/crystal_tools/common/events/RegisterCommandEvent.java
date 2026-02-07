package dev.willyelton.crystal_tools.common.events;

import com.mojang.brigadier.CommandDispatcher;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.command.AddPointsBlockCommand;
import dev.willyelton.crystal_tools.common.command.AddPointsCommand;
import dev.willyelton.crystal_tools.common.command.AddPointsEntityCommand;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class RegisterCommandEvent {
    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();

        AddPointsCommand.register(commandDispatcher);
        AddPointsBlockCommand.register(commandDispatcher);
        AddPointsEntityCommand.register(commandDispatcher);
    }
}
