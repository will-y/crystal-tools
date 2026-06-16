package dev.willyelton.crystal.tools.datagen;

import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.tags.CrystalToolsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class CrystalToolsBlockTags extends BlockTagsProvider {
    public CrystalToolsBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, CrystalTools.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                ModRegistration.CRYSTAL_FURNACE.getKey(),
                ModRegistration.CRYSTAL_GENERATOR.getKey(),
                ModRegistration.CRYSTAL_QUARRY.getKey(),
                ModRegistration.QUARRY_STABILIZER.getKey(),
                ModRegistration.CRYSTAL_PEDESTAL.getKey());

        tag(CrystalToolsTags.MINABLE_WITH_AIOT)
                .addTag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(BlockTags.MINEABLE_WITH_HOE)
                .addTag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(BlockTags.MINEABLE_WITH_SHOVEL)
                .addTag(BlockTags.SWORD_EFFICIENT);
    }
}
