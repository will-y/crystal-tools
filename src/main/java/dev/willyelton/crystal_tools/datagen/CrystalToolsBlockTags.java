package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class CrystalToolsBlockTags extends BlockTagsProvider {
    public CrystalToolsBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, CrystalTools.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModRegistration.CRYSTAL_BLOCK.get(),
                ModRegistration.CRYSTAL_ORE.get(),
                ModRegistration.CRYSTAL_DEEPSLATE_ORE.get(),
                ModRegistration.CRYSTAL_FURNACE.get(),
                ModRegistration.CRYSTAL_GENERATOR.get(),
                ModRegistration.CRYSTAL_QUARRY.get(),
                ModRegistration.QUARRY_STABILIZER.get(),
                ModRegistration.CRYSTAL_GEODE.get(),
                ModRegistration.NETHERITE_INFUSED_CRYSTAL_GEODE.get(),
                ModRegistration.CRYSTAL_PEDESTAL.get());

        tag(CrystalToolsTags.AUTO_OUTPUT_BLACKLIST).add(
                Blocks.HOPPER);

        tag(CrystalToolsTags.MINABLE_WITH_AIOT)
                .addTag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(BlockTags.MINEABLE_WITH_HOE)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(BlockTags.MINEABLE_WITH_SHOVEL)
                .addTag(BlockTags.SWORD_EFFICIENT);
    }
}
