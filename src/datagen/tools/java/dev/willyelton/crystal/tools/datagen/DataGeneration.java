package dev.willyelton.crystal.tools.datagen;

import dev.willyelton.crystal.core.common.event.DatapackRegistryEvents;
import dev.willyelton.crystal.tools.CrystalTools;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = CrystalTools.MODID)
public class DataGeneration {

    @SubscribeEvent
    public static void generate(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getWorldLookupProvider();

        generator.addProvider(true, new CrystalToolsModels(packOutput));

        CrystalToolsBlockTags blockTags = new CrystalToolsBlockTags(packOutput, lookupProvider);
        generator.addProvider(true, blockTags);

        CrystalToolsItemTags itemTags = new CrystalToolsItemTags(packOutput, lookupProvider);
        generator.addProvider(true, itemTags);

        CrystalToolsEntityTypeTags entityTypeTags = new CrystalToolsEntityTypeTags(packOutput, lookupProvider);
        generator.addProvider(true, entityTypeTags);

        CrystalToolsDataMaps dataMaps = new CrystalToolsDataMaps(packOutput, lookupProvider);
        generator.addProvider(true, dataMaps);
    }

    @SubscribeEvent
    public static void generateRegistryEntries(GatherDataRegistryEntriesEvent event) {
        event.gatherFor(CrystalTools.MODID)
                .recipe(CrystalToolsRecipes::new);

        event.gatherFor(CrystalTools.MODID, "minecraft")
                .add(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ITEMS, CrystalToolsItemSkillTrees::register)
                .add(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_BLOCKS, CrystalToolsBlockSkillTrees::register)
                .add(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY_ENTITIES, CrystalToolsEntitySkillTrees::register);
    }
}
