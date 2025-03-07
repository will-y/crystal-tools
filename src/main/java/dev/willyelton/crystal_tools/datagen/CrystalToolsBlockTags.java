package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class CrystalToolsBlockTags extends BlockTagsProvider {
    public CrystalToolsBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CrystalTools.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                Registration.CRYSTAL_BLOCK.get(),
                Registration.CRYSTAL_ORE.get(),
                Registration.CRYSTAL_DEEPSLATE_ORE.get(),
                Registration.CRYSTAL_FURNACE.get(),
                Registration.CRYSTAL_GENERATOR.get(),
                Registration.CRYSTAL_QUARRY.get(),
                Registration.QUARRY_STABILIZER.get());

        tag(CrystalToolsTags.AUTO_OUTPUT_BLACKLIST).add(
                Blocks.HOPPER);
    }
}
