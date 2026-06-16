package dev.willyelton.crystal.core.datagen;

import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ApiConstants.CORE_MOD_ID)
public class CoreDataGeneration {
    @SubscribeEvent
    public static void generate(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new CrystalCoreModels(packOutput));

        CrystalCoreBlockTags blockTags = new CrystalCoreBlockTags(packOutput, lookupProvider);
        generator.addProvider(true, blockTags);

        CrystalCoreItemTags itemTags = new CrystalCoreItemTags(packOutput, lookupProvider);
        generator.addProvider(true, itemTags);

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(CrystalCoreLootTables::new, LootContextParamSets.BLOCK)), event.getLookupProvider()));

        event.createProvider(CrystalCoreRecipes.Runner::new);

        CrystalCoreDataMapGenerator dataMaps = new CrystalCoreDataMapGenerator(packOutput, lookupProvider);
        generator.addProvider(true, dataMaps);
    }
}
