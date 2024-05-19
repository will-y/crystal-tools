package dev.willyelton.crystal_tools.event;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.CrystalBackpack;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalTools.MODID)
public class BlockEvents {
    @SubscribeEvent
    public static void handleBlockBreakEvent(BlockEvent.BreakEvent event) {
        System.out.println("Event Triggered");
        CrystalBackpack.addXpToBackpacks(event.getPlayer(), 1);
    }
}
