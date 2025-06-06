package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.datamap.GeneratorFuelData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class CrystalToolsDataMaps extends DataMapProvider {
    public CrystalToolsDataMaps(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(DataMaps.GENERATOR_GEMS)
                .add(Registration.CRYSTAL, new GeneratorFuelData(10800, 100), false)
                .add(Items.AMETHYST_SHARD.builtInRegistryHolder(), new GeneratorFuelData(1800, 20), false)
                .add(Items.DIAMOND.builtInRegistryHolder(), new GeneratorFuelData(5400, 80), false)
                .add(Items.EMERALD.builtInRegistryHolder(), new GeneratorFuelData(3600, 40), false)
                .add(Items.LAPIS_LAZULI.builtInRegistryHolder(), new GeneratorFuelData(1800, 20), false)
                .add(Items.PRISMARINE_CRYSTALS.builtInRegistryHolder(), new GeneratorFuelData(1800, 20), false)
                .add(Items.PRISMARINE_SHARD.builtInRegistryHolder(), new GeneratorFuelData(1800, 20), false)
                .add(Items.QUARTZ.builtInRegistryHolder(), new GeneratorFuelData(1800, 20), false)
                .add(Registration.CRYSTAL_BLOCK_ITEM, new GeneratorFuelData(97200, 100), false)
                .add(Items.AMETHYST_BLOCK.builtInRegistryHolder(), new GeneratorFuelData(16200, 20), false)
                .add(Items.DIAMOND_BLOCK.builtInRegistryHolder(), new GeneratorFuelData(48600, 80), false)
                .add(Items.EMERALD_BLOCK.builtInRegistryHolder(), new GeneratorFuelData(32400, 40), false)
                .add(Items.LAPIS_BLOCK.builtInRegistryHolder(), new GeneratorFuelData(16200, 20), false)
                .add(Items.PRISMARINE.builtInRegistryHolder(), new GeneratorFuelData(7200, 20), false)
                .add(Items.PRISMARINE_BRICKS.builtInRegistryHolder(), new GeneratorFuelData(16200, 20), false)
                .add(Items.DARK_PRISMARINE.builtInRegistryHolder(), new GeneratorFuelData(14400, 20), false)
                .add(Items.QUARTZ_BRICKS.builtInRegistryHolder(), new GeneratorFuelData(7200, 20), false)
                .add(Items.QUARTZ_PILLAR.builtInRegistryHolder(), new GeneratorFuelData(7200, 20), false)
                .add(Tags.Items.GEMS, new GeneratorFuelData(800, 5), false)
                // TODO: Add to some mods with conditions
                .build();

        builder(DataMaps.GENERATOR_METALS)
                .add(Items.IRON_INGOT.builtInRegistryHolder(), new GeneratorFuelData(1800, 5), false)
                .add(Items.IRON_BLOCK.builtInRegistryHolder(), new GeneratorFuelData(16200, 5), false)
                .add(Items.IRON_NUGGET.builtInRegistryHolder(), new GeneratorFuelData(200, 5), false)
                .add(Items.GOLD_INGOT.builtInRegistryHolder(), new GeneratorFuelData(2700, 10), false)
                .add(Items.GOLD_BLOCK.builtInRegistryHolder(), new GeneratorFuelData(24300, 10), false)
                .add(Items.GOLD_NUGGET.builtInRegistryHolder(), new GeneratorFuelData(300, 10), false)
                .add(Items.NETHERITE_INGOT.builtInRegistryHolder(), new GeneratorFuelData(10800, 100), false)
                .add(Items.NETHERITE_BLOCK.builtInRegistryHolder(), new GeneratorFuelData(97200, 100), false)
                .add(Tags.Items.INGOTS, new GeneratorFuelData(800, 5), false)
                .build();

        builder(DataMaps.MOB_HEADS)
                .add(EntityType.ZOMBIE.builtInRegistryHolder(), Items.ZOMBIE_HEAD, false)
                .add(EntityType.CREEPER.builtInRegistryHolder(), Items.CREEPER_HEAD, false)
                .add(EntityType.PIGLIN.builtInRegistryHolder(), Items.PIGLIN_HEAD, false)
                .add(EntityType.PIGLIN_BRUTE.builtInRegistryHolder(), Items.PIGLIN_HEAD, false)
                .add(EntityType.ENDER_DRAGON.builtInRegistryHolder(), Items.DRAGON_HEAD, false)
                .add(EntityType.SKELETON.builtInRegistryHolder(), Items.SKELETON_SKULL, false)
                .add(EntityType.WITHER_SKELETON.builtInRegistryHolder(), Items.WITHER_SKELETON_SKULL, false)
                .add(EntityType.PLAYER.builtInRegistryHolder(), Items.PLAYER_HEAD, false);
    }
}
