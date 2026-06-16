package dev.willyelton.crystal.core.datagen;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class CrystalCoreBlockTags extends BlockTagsProvider {

    public CrystalCoreBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ApiConstants.CORE_MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                Registration.CRYSTAL_BLOCK.getKey(),
                Registration.CRYSTAL_ORE.getKey(),
                Registration.CRYSTAL_DEEPSLATE_ORE.getKey(),
                Registration.NETHERITE_INFUSED_CRYSTAL_GEODE.getKey());
    }
}
