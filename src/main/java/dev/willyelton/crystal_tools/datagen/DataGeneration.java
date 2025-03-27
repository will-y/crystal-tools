package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.common.events.DatapackRegistryEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataGeneration {
    public static void generate(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new CrystalToolsModels(packOutput));

        CrystalToolsBlockTags blockTags = new CrystalToolsBlockTags(packOutput, lookupProvider);
        generator.addProvider(true, blockTags);

        CrystalToolsItemTags itemTags = new CrystalToolsItemTags(packOutput, lookupProvider, blockTags.contentsGetter());
        generator.addProvider(true, itemTags);

        CrystalToolsEntityTypeTags entityTypeTags = new CrystalToolsEntityTypeTags(packOutput, lookupProvider);
        generator.addProvider(true, entityTypeTags);

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(CrystalToolsLootTables::new, LootContextParamSets.BLOCK)), event.getLookupProvider()));

        event.createProvider(CrystalToolsRecipes.Runner::new);

        CrystalToolsDataMaps dataMaps = new CrystalToolsDataMaps(packOutput, lookupProvider);
        generator.addProvider(true, dataMaps);

        event.createDatapackRegistryObjects(
                new RegistrySetBuilder().add(DatapackRegistryEvents.SKILL_DATA_REGISTRY_KEY, CrystalToolsSkillTrees::skillTrees)
        );
    }
}
