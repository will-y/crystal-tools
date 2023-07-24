package dev.willyelton.crystal_tools.world;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.block.ModBlocks;
import dev.willyelton.crystal_tools.config.CrystalToolsConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModOres {

//    private static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, CrystalTools.MODID);
//
//    public static final RegistryObject<PlacedFeature> ORE_DEEPSLATE_OVERWORLD = PLACED_FEATURES.register("crystal_deepslate_ore", ModOres::createOverworldDeepslateOregen);
////    public static final RegistryObject<PlacedFeature> ORE_OVERWORLD = PLACED_FEATURES.register("crystal_ore", ModOres::createOverworldOregen);
//
//    public static void initOres() {
//        PLACED_FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
//    }
//
//    @NotNull
//    public static PlacedFeature createOverworldDeepslateOregen() {
//        OreConfiguration config = new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.CRYSTAL_DEEPSLATE_ORE.get().defaultBlockState(), 5);
//        return createPlacedFeature(new ConfiguredFeature<>(Feature.ORE, config),
//                CountPlacement.of(2),
//                InSquarePlacement.spread(),
//                HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(0), VerticalAnchor.aboveBottom(20)));
//    }
//
//    @NotNull
//    public static PlacedFeature createOverworldOregen() {
//        OreConfiguration config = new OreConfiguration(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.CRYSTAL_ORE.get().defaultBlockState(), CrystalToolsConfig.STONE_VEIN_SIZE.get());
//        return createPlacedFeature(new ConfiguredFeature<>(Feature.ORE, config),
//                CountPlacement.of(CrystalToolsConfig.STONE_PER_CHUNK.get()),
//                InSquarePlacement.spread(),
//                HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(CrystalToolsConfig.STONE_BOTTOM.get()), VerticalAnchor.aboveBottom(CrystalToolsConfig.STONE_TOP.get())));
//    }
//
//    private static <C extends FeatureConfiguration, F extends Feature<C>> PlacedFeature createPlacedFeature(ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
//        return new PlacedFeature(Holder.hackyErase(Holder.direct(feature)), List.copyOf(List.of(placementModifiers)));
//    }
}
