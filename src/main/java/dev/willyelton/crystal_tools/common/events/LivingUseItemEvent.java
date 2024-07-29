package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.tool.BowLevelableItem;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class LivingUseItemEvent {
    @SubscribeEvent
    public static void livingUseItem(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player && event.getItem().getItem() instanceof BowLevelableItem bowItem) {
            event.setDuration(event.getDuration() - (20 - (int) bowItem.getChargeTime(event.getItem())));
        }
    }
}
