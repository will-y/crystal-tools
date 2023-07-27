package dev.willyelton.crystal_tools.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DataGeneration {
    public static void generate(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new CrystalToolsBlockStates(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new CrystalToolsItemModels(packOutput, event.getExistingFileHelper()));

        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(CrystalToolsLootTables::new, LootContextParamSets.BLOCK))));
        generator.addProvider(event.includeServer(), new CrystalToolsRecipes(packOutput));
    }
}
