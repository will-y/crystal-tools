package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.tool.LevelableTool;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModifyDefaultComponentsEvent {
    @SubscribeEvent
    public static void handleDefaultComponents(net.neoforged.neoforge.event.ModifyDefaultComponentsEvent event) {
        // Remove the enchantable component from my tools (can't do in props with constructor)
        event.modifyMatching(item -> item instanceof LevelableTool, builder -> builder.remove(DataComponents.ENCHANTABLE));
    }
}
