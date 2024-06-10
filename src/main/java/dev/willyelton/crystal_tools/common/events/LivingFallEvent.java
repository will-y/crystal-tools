package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.Registration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class LivingFallEvent {
    @SubscribeEvent
    public static void handleLivingFallEvent(net.neoforged.neoforge.event.entity.living.LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (ItemStack stack : player.getArmorSlots()) {
                if (stack.is(Registration.CRYSTAL_BOOTS.get()) && stack.getOrDefault(DataComponents.NO_FALL_DAMAGE, false)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
