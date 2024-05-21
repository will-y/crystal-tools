package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID)
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
