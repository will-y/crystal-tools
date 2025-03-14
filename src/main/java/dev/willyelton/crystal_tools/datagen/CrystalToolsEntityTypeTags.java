package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class CrystalToolsEntityTypeTags extends EntityTypeTagsProvider {
    public CrystalToolsEntityTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, CrystalTools.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(CrystalToolsTags.ENTITY_BLACKLIST).add(
                EntityType.ARMOR_STAND);
    }
}
