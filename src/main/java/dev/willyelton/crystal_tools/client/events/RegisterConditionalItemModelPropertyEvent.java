package dev.willyelton.crystal_tools.client.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.client.model.property.Disabled;
import dev.willyelton.crystal_tools.client.model.property.Lit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

@EventBusSubscriber(modid = CrystalTools.MODID, value = Dist.CLIENT)
public class RegisterConditionalItemModelPropertyEvent {
    @SubscribeEvent
    public static void registerConditionalProperties(net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent event) {
        event.register(rl("disabled"), Disabled.MAP_CODEC);
        event.register(rl("lit"), Lit.MAP_CODEC);
    }
}
