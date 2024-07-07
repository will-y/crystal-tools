package dev.willyelton.crystal_tools.common.events;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.levelable.CrystalBackpack;
import dev.willyelton.crystal_tools.common.levelable.tool.LevelableTool;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class BlockEvents {
    @SubscribeEvent
    public static void breakEvent(BlockEvent.BreakEvent event) {
        // Backpack levels
        CrystalBackpack.addXpToBackpacks(event.getPlayer(), 1);

        // onBlockStartBreak replacement
        ItemStack stack = event.getPlayer().getMainHandItem();
        if (stack.getItem() instanceof LevelableTool levelableTool) {
            boolean result = levelableTool.onBlockStartBreak(stack, event.getPos(), event.getPlayer(), event.getState());

            if (result) {
                event.setCanceled(true);
            }
        }
    }
}