package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.datamap.GeneratorFuelData;
import dev.willyelton.crystal_tools.common.datamap.SkillTreeData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

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

        var builder = builder(DataMaps.SKILL_TREES)
                .add(Registration.CRYSTAL_PICKAXE, new SkillTreeData(rl("crystal_pickaxe")), false)
                .add(Registration.CRYSTAL_HOE, new SkillTreeData(rl("crystal_hoe")), false)
                .add(Registration.CRYSTAL_SHOVEL, new SkillTreeData(rl("crystal_shovel")), false)
                .add(Registration.CRYSTAL_AXE, new SkillTreeData(rl("crystal_axe")), false)
                .add(Registration.CRYSTAL_AIOT, new SkillTreeData(rl("crystal_aiot")), false)
                .add(Registration.CRYSTAL_FISHING_ROD, new SkillTreeData(rl("crystal_fishing_rod")), false)
                .add(Registration.CRYSTAL_SHIELD, new SkillTreeData(rl("crystal_shield")), false)
                .add(Registration.CRYSTAL_SWORD, new SkillTreeData(rl("crystal_sword"), 0.8F), false)
                .add(Registration.CRYSTAL_BOW, new SkillTreeData(rl("crystal_bow")), false)
                .add(Registration.CRYSTAL_TRIDENT, new SkillTreeData(rl("crystal_trident")), false)
                .add(Registration.CRYSTAL_APPLE, new SkillTreeData(rl("crystal_apple"), 0.5F), false)
                .add(Registration.CRYSTAL_HELMET, new SkillTreeData(rl("crystal_helmet"), 2.0F), false)
                .add(Registration.CRYSTAL_CHESTPLATE, new SkillTreeData(rl("crystal_chestplate"), 2.0F), false)
                .add(Registration.CRYSTAL_LEGGINGS, new SkillTreeData(rl("crystal_leggings"), 2.0F), false)
                .add(Registration.CRYSTAL_BOOTS, new SkillTreeData(rl("crystal_boots"), 2.0F), false)
                .add(Registration.CRYSTAL_ELYTRA, new SkillTreeData(rl("crystal_elytra")), false)
                .add(Registration.CRYSTAL_ROCKET, new SkillTreeData(rl("crystal_rocket"), 5.0F), false)
                .add(Registration.CRYSTAL_BACKPACK, new SkillTreeData(rl("crystal_backpack")), false);

        builder.add(Items.DIAMOND_PICKAXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_pickaxe"), 0, 1, false, false, false), false)
                .add(Items.DIAMOND_AXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_axe"), 0, 1, false, false, false), false)
                .add(Items.DIAMOND_HOE.builtInRegistryHolder(), new SkillTreeData(rl("simple_hoe"), 0, 1, false, false, false), false)
                .add(Items.DIAMOND_SHOVEL.builtInRegistryHolder(), new SkillTreeData(rl("simple_shovel"), 0, 1, false, false, false), false)
                .add(Items.DIAMOND_SWORD.builtInRegistryHolder(), new SkillTreeData(rl("simple_sword"), 0, 1, false, false, false), false)

                .add(Items.IRON_PICKAXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_pickaxe"), 0, 2, false, false, false), false)
                .add(Items.IRON_AXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_axe"), 0, 2, false, false, false), false)
                .add(Items.IRON_HOE.builtInRegistryHolder(), new SkillTreeData(rl("simple_hoe"), 0, 2, false, false, false), false)
                .add(Items.IRON_SHOVEL.builtInRegistryHolder(), new SkillTreeData(rl("simple_shovel"), 0, 2, false, false, false), false)
                .add(Items.IRON_SWORD.builtInRegistryHolder(), new SkillTreeData(rl("simple_sword"), 0, 2, false, false, false), false)

                .add(Items.GOLDEN_PICKAXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_pickaxe"), -20, 2, false, false, false), false)
                .add(Items.GOLDEN_AXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_axe"), -20, 2, false, false, false), false)
                .add(Items.GOLDEN_HOE.builtInRegistryHolder(), new SkillTreeData(rl("simple_hoe"), -20, 2, false, false, false), false)
                .add(Items.GOLDEN_SHOVEL.builtInRegistryHolder(), new SkillTreeData(rl("simple_shovel"), -20, 2, false, false, false), false)
                .add(Items.GOLDEN_SWORD.builtInRegistryHolder(), new SkillTreeData(rl("simple_sword"), -20, 2, false, false, false), false)

                .add(Items.STONE_PICKAXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_pickaxe"), -10, 2, false, false, false), false)
                .add(Items.STONE_AXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_axe"), -10, 2, false, false, false), false)
                .add(Items.STONE_HOE.builtInRegistryHolder(), new SkillTreeData(rl("simple_hoe"), -10, 2, false, false, false), false)
                .add(Items.STONE_SHOVEL.builtInRegistryHolder(), new SkillTreeData(rl("simple_shovel"), -10, 2, false, false, false), false)
                .add(Items.STONE_SWORD.builtInRegistryHolder(), new SkillTreeData(rl("simple_sword"), -10, 2, false, false, false), false)

                .add(Items.WOODEN_PICKAXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_pickaxe"), -20, 2, false, false, false), false)
                .add(Items.WOODEN_AXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_axe"), -20, 2, false, false, false), false)
                .add(Items.WOODEN_HOE.builtInRegistryHolder(), new SkillTreeData(rl("simple_hoe"), -20, 2, false, false, false), false)
                .add(Items.WOODEN_SHOVEL.builtInRegistryHolder(), new SkillTreeData(rl("simple_shovel"), -20, 2, false, false, false), false)
                .add(Items.WOODEN_SWORD.builtInRegistryHolder(), new SkillTreeData(rl("simple_sword"), -20, 2, false, false, false), false);

        builder.add(Items.NETHERITE_PICKAXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_pickaxe"), 0, 1, false, false, false), false)
                .add(Items.NETHERITE_AXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_axe"), 0, 1, false, false, false), false)
                .add(Items.NETHERITE_HOE.builtInRegistryHolder(), new SkillTreeData(rl("simple_hoe"), 0, 1, false, false, false), false)
                .add(Items.NETHERITE_SHOVEL.builtInRegistryHolder(), new SkillTreeData(rl("simple_shovel"), 0, 1, false, false, false), false)
                .add(Items.NETHERITE_SWORD.builtInRegistryHolder(), new SkillTreeData(rl("simple_sword"), 0, 1, false, false, false), false);
    }
}
