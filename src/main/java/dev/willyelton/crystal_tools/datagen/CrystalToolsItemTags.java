package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CrystalToolsItemTags extends ItemTagsProvider {
    public CrystalToolsItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, CrystalTools.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(Tags.Items.GEMS).add(
                Registration.CRYSTAL.get());

        tag(Tags.Items.RODS).add(
                Registration.NETHERITE_STICK.get());

        tag(CrystalToolsTags.RODS_METAL).add(
                Registration.NETHERITE_STICK.get());

        tag(CrystalToolsTags.RODS_METAL_NETHERITE).add(
                Registration.NETHERITE_STICK.get());

        tag(ItemTags.PICKAXES).add(
                Registration.CRYSTAL_PICKAXE.get(),
                Registration.CRYSTAL_AIOT.get());

        tag(ItemTags.AXES).add(
                Registration.CRYSTAL_AXE.get(),
                Registration.CRYSTAL_AIOT.get());

        tag(ItemTags.SHOVELS).add(
                Registration.CRYSTAL_SHOVEL.get(),
                Registration.CRYSTAL_AIOT.get());

        tag(ItemTags.HOES).add(
                Registration.CRYSTAL_HOE.get(),
                Registration.CRYSTAL_AIOT.get());

        tag(ItemTags.SWORDS).add(
                Registration.CRYSTAL_SWORD.get(),
                Registration.CRYSTAL_AIOT.get());

        tag(ItemTags.BOW_ENCHANTABLE).add(
                Registration.CRYSTAL_BOW.get());

        tag(ItemTags.TRIDENT_ENCHANTABLE).add(
                Registration.CRYSTAL_TRIDENT.get());

        tag(Tags.Items.TOOLS_SPEAR).add(
                Registration.CRYSTAL_TRIDENT.get());

        tag(Tags.Items.TOOLS_FISHING_ROD).add(
                Registration.CRYSTAL_FISHING_ROD.get());

        tag(ItemTags.HEAD_ARMOR).add(
                Registration.CRYSTAL_HELMET.get());

        tag(ItemTags.CHEST_ARMOR).add(
                Registration.CRYSTAL_CHESTPLATE.get(),
                Registration.CRYSTAL_ELYTRA.get());

        tag(ItemTags.LEG_ARMOR).add(
                Registration.CRYSTAL_LEGGINGS.get());

        tag(ItemTags.FOOT_ARMOR).add(
                Registration.CRYSTAL_BOOTS.get());

        tag(CrystalToolsTags.CRYSTAL_TOOL).add(
                Registration.CRYSTAL_PICKAXE.get(),
                Registration.CRYSTAL_AXE.get(),
                Registration.CRYSTAL_SHOVEL.get(),
                Registration.CRYSTAL_HOE.get(),
                Registration.CRYSTAL_SWORD.get(),
                Registration.CRYSTAL_BOW.get(),
                Registration.CRYSTAL_AIOT.get(),
                Registration.CRYSTAL_ROCKET.get(),
                Registration.CRYSTAL_TRIDENT.get(),
                Registration.CRYSTAL_FISHING_ROD.get(),
                Registration.CRYSTAL_SHIELD.get(),
                Registration.CRYSTAL_HELMET.get(),
                Registration.CRYSTAL_CHESTPLATE.get(),
                Registration.CRYSTAL_LEGGINGS.get(),
                Registration.CRYSTAL_BOOTS.get(),
                Registration.CRYSTAL_ELYTRA.get());
    }
}
