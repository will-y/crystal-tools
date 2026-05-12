package dev.willyelton.crystal.core.common.event;

import dev.willyelton.crystal.core.common.datacomponent.DataComponents;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class LivingFallEvent {
    @SubscribeEvent
    public static void handleLivingFallEvent(net.neoforged.neoforge.event.entity.living.LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
            if (stack.getOrDefault(DataComponents.NO_FALL_DAMAGE, false)) {
                event.setCanceled(true);
            }
        }
    }
}
