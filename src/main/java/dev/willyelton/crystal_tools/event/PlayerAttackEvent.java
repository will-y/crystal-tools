package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID)
public class PlayerAttackEvent {
    @SubscribeEvent
    public static void handleAttackEntityEvent(AttackEntityEvent event) {
        CrystalBackpack.addXpToBackpacks(event.getEntity(), 2);
    }
}
