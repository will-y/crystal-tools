package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class PlayerAttackEvent {
    @SubscribeEvent
    public static void handleAttackEntityEvent(AttackEntityEvent event) {
        if (event.getTarget() instanceof LivingEntity target) {
            if (ToolUtils.isValidEntity(target)) {
                CrystalBackpack.addXpToBackpacks(event.getEntity(), 2);
            }
        }
    }
}
