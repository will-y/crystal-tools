package dev.willyelton.crystal_tools.datagen.vanilla;

import dev.willyelton.crystal_tools.CrystalTools;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

// TODO: This should only be used for datamap now?
/**
 * This is going to be a little hacky.
 * Using different run configs and parameters to determine where
 * the pack output should go. Also using the server event here
 * because I don't want to just have a boolean in the other event,
 * and I'm pretty sure the events are the same
 */
@EventBusSubscriber(modid = CrystalTools.MODID, bus = EventBusSubscriber.Bus.MOD)
public class VanillaTreesDataGeneration {
    @SubscribeEvent
    public static void generate(GatherDataEvent.Server event) {
        boolean full = System.getProperty("netherite") == null;

        var dataMaps = new VanillaDataMaps(event.getGenerator().getPackOutput(), event.getLookupProvider(), full);
        event.getGenerator().addProvider(true, dataMaps);
    }
}
