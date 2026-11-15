package dev.willyelton.crystal.core.datagen;

import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class CoreDataGeneration {
    @SubscribeEvent
    public static void generate(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getWorldLookupProvider();

        generator.addProvider(true, new CrystalCoreModels(packOutput));

        CrystalCoreBlockTags blockTags = new CrystalCoreBlockTags(packOutput, lookupProvider);
        generator.addProvider(true, blockTags);

        CrystalCoreItemTags itemTags = new CrystalCoreItemTags(packOutput, lookupProvider);
        generator.addProvider(true, itemTags);

        CrystalCoreDataMapGenerator dataMaps = new CrystalCoreDataMapGenerator(packOutput, lookupProvider);
        generator.addProvider(true, dataMaps);
    }

    @SubscribeEvent
    public static void generateRegistryEntries(GatherDataRegistryEntriesEvent event) {
        event.gatherFor(ApiConstants.CORE_MOD_ID)
                .recipe(CrystalCoreRecipes::new)
                .lootTable(new LootTableProvider.SubProviderEntry(CrystalCoreLootTables::new, LootContextParamSets.BLOCK));
    }
}
