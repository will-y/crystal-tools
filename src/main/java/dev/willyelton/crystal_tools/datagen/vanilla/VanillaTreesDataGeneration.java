package dev.willyelton.crystal_tools.datagen.vanilla;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import dev.willyelton.crystal_tools.datagen.MinecraftItemSkillTrees;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;

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
        event.createDatapackRegistryObjects(
                new RegistrySetBuilder().add(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS, context -> {
                    MinecraftItemSkillTrees.registerSkillTrees(context, System.getProperty("netherite") == null);
                }),
                Set.of("minecraft"));
    }
}
