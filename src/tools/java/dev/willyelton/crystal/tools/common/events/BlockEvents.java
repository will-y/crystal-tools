package dev.willyelton.crystal.tools.common.events;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.common.levelable.CrystalBackpack;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class BlockEvents {
    @SubscribeEvent
    public static void breakEvent(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();

        // Backpack levels
        CrystalBackpack.addXpToBackpacks(player, 1);
    }
}
