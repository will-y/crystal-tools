package dev.willyelton.crystal_tools.world;

import dev.willyelton.crystal_tools.block.ModBlocks;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.event.level.LevelEvent;


// TY Mcjty
// https://wiki.mcjty.eu/modding/index.php?title=Tutorial_1.18_Episode_5#Ore_Generation

public class ModOres {
    public static Holder<PlacedFeature> OVERWORLD_OREGEN;
    public static Holder<PlacedFeature> DEEPSLATE_OREGEN;

    public static void registerConfiguredFeatures() {
        // Stone Overworld - for now doesn't spawn there
        OreConfiguration overworldConfig = new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES,
                ModBlocks.CRYSTAL_ORE.get().defaultBlockState(), CrystalToolsConfig.STONE_VEIN_SIZE.get());

        DEEPSLATE_OREGEN = registerPlacedFeature("crystal_deepslate_ore", new ConfiguredFeature<>(Feature.ORE, overworldConfig),
                CountPlacement.of(CrystalToolsConfig.STONE_PER_CHUNK.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(CrystalToolsConfig.STONE_BOTTOM.get()), VerticalAnchor.aboveBottom(CrystalToolsConfig.STONE_TOP.get())));

        // Deepslate Overworld
        OreConfiguration deepslateConfig = new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES,
                ModBlocks.CRYSTAL_DEEPSLATE_ORE.get().defaultBlockState(), CrystalToolsConfig.DEEPSLATE_VEIN_SIZE.get());

        DEEPSLATE_OREGEN = registerPlacedFeature("crystal_deepslate_ore", new ConfiguredFeature<>(Feature.ORE, deepslateConfig),
                CountPlacement.of(CrystalToolsConfig.DEEPSLATE_PER_CHUNK.get()),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(CrystalToolsConfig.DEEPSLATE_BOTTOM.get()), VerticalAnchor.aboveBottom(CrystalToolsConfig.DEEPSLATE_TOP.get())));
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> Holder<PlacedFeature> registerPlacedFeature(String registryName, ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
        return PlacementUtils.register(registryName, Holder.direct(feature), placementModifiers);
    }

    // TODO: Reimplement Ore Gen
//    public static void onBiomeLoadingEvent(LevelEvent event) {
//        // for now only overworld generation
//        if (event.getCategory() != Biome.BiomeCategory.NETHER && event.getCategory() != Biome.BiomeCategory.THEEND) {
////            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OVERWORLD_OREGEN);
//            if (CrystalToolsConfig.GENERATE_DEEPSLATE_ORE.get()) {
//                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, DEEPSLATE_OREGEN);
//            }
//            if (CrystalToolsConfig.GENERATE_STONE_ORE.get()) {
//                event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, OVERWORLD_OREGEN);
//            }
//        }
//    }
}
