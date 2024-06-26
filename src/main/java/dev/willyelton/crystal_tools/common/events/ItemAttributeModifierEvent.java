package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.LevelableItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class ItemAttributeModifierEvent {
    @SubscribeEvent
    public static void handleItemAttributeModifier(net.neoforged.neoforge.event.ItemAttributeModifierEvent event) {
        if (event.getItemStack().getItem() instanceof LevelableItem levelableItem) {
            ItemAttributeModifiers modifiers = levelableItem.getLevelableAttributeModifiers(event.getItemStack());
            modifiers.modifiers().forEach(entry -> {
                event.addModifier(entry.attribute(), entry.modifier(), entry.slot());
            });
        }
    }
}
