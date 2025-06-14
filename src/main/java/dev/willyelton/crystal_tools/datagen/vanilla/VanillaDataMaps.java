package dev.willyelton.crystal_tools.datagen.vanilla;

import dev.willyelton.crystal_tools.common.datamap.DataMaps;
import dev.willyelton.crystal_tools.common.datamap.SkillTreeData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

import static dev.willyelton.crystal_tools.CrystalTools.rl;

public class VanillaDataMaps extends DataMapProvider {
    private final boolean full;

    public VanillaDataMaps(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, boolean full) {
        super(packOutput, lookupProvider);
        this.full = full;
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        var builder = builder(DataMaps.SKILL_TREES);
        if (full) {
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

            addNetherite(builder);
        } else {
            addNetherite(builder);
        }
    }

    private void addNetherite(DataMapProvider.Builder<SkillTreeData, Item> builder) {
        builder.add(Items.NETHERITE_PICKAXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_pickaxe"), 0, 1, false, false, false), false)
                .add(Items.NETHERITE_AXE.builtInRegistryHolder(), new SkillTreeData(rl("simple_axe"), 0, 1, false, false, false), false)
                .add(Items.NETHERITE_HOE.builtInRegistryHolder(), new SkillTreeData(rl("simple_hoe"), 0, 1, false, false, false), false)
                .add(Items.NETHERITE_SHOVEL.builtInRegistryHolder(), new SkillTreeData(rl("simple_shovel"), 0, 1, false, false, false), false)
                .add(Items.NETHERITE_SWORD.builtInRegistryHolder(), new SkillTreeData(rl("simple_sword"), 0, 1, false, false, false), false);
    }
}
