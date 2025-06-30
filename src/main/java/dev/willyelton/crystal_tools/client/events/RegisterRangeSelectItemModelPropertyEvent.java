package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.renderer.item.properties.BowUseDuration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class RegisterRangeSelectItemModelPropertyEvent {
    @SubscribeEvent
    public static void registerRangeSelectItemModelPropertyEvent(net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "bow_use_duration"), BowUseDuration.MAP_CODEC);
    }
}
