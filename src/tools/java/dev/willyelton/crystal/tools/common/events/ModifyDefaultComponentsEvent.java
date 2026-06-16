package dev.willyelton.crystal.tools.common.events;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.common.levelable.tool.LevelableTool;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ModifyDefaultComponentsEvent {
    @SubscribeEvent
    public static void handleDefaultComponents(net.neoforged.neoforge.event.ModifyDefaultComponentsEvent event) {
        // Remove the enchantable component from my tools (can't do in props with constructor)
        event.modifyMatching((item, _) -> item instanceof LevelableTool, (components, _, _) -> components.set(DataComponents.ENCHANTABLE, null));
    }
}
