package dev.willyelton.crystal.core.datagen;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.common.tag.CrystalCoreTags;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CrystalCoreItemTags extends TagsProvider<Item> {
    public CrystalCoreItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider, ApiConstants.CORE_MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.getOrCreateRawBuilder(Tags.Items.RODS).addElement(
                Registration.NETHERITE_STICK.getId());

        this.getOrCreateRawBuilder(CrystalCoreTags.RODS_METAL).addElement(
                Registration.NETHERITE_STICK.getId());

        this.getOrCreateRawBuilder(CrystalCoreTags.RODS_METAL_NETHERITE).addElement(
                Registration.NETHERITE_STICK.getId());
    }
}
