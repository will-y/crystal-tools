package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CrystalToolsItemTags extends TagsProvider<Item> {
    public CrystalToolsItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider, CrystalTools.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.getOrCreateRawBuilder(Tags.Items.GEMS).addElement(
                Registration.CRYSTAL.getId());

        this.getOrCreateRawBuilder(Tags.Items.RODS).addElement(
                Registration.NETHERITE_STICK.getId());

        this.getOrCreateRawBuilder(CrystalToolsTags.RODS_METAL).addElement(
                Registration.NETHERITE_STICK.getId());

        this.getOrCreateRawBuilder(CrystalToolsTags.RODS_METAL_NETHERITE).addElement(
                Registration.NETHERITE_STICK.getId());

        this.getOrCreateRawBuilder(ItemTags.PICKAXES)
                .addElement(Registration.CRYSTAL_PICKAXE.getId())
                .addElement(Registration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.AXES)
                .addElement(Registration.CRYSTAL_AXE.getId())
                .addElement(Registration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.SHOVELS)
                .addElement(Registration.CRYSTAL_SHOVEL.getId())
                .addElement(Registration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.HOES)
                .addElement(Registration.CRYSTAL_HOE.getId())
                .addElement(Registration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.SWORDS)
                .addElement(Registration.CRYSTAL_SWORD.getId())
                .addElement(Registration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.BOW_ENCHANTABLE).addElement(
                Registration.CRYSTAL_BOW.getId());

        this.getOrCreateRawBuilder(ItemTags.TRIDENT_ENCHANTABLE).addElement(
                Registration.CRYSTAL_TRIDENT.getId());

        this.getOrCreateRawBuilder(Tags.Items.TOOLS_SPEAR).addElement(
                Registration.CRYSTAL_TRIDENT.getId());

        this.getOrCreateRawBuilder(Tags.Items.TOOLS_FISHING_ROD).addElement(
                Registration.CRYSTAL_FISHING_ROD.getId());

        this.getOrCreateRawBuilder(ItemTags.HEAD_ARMOR).addElement(
                Registration.CRYSTAL_HELMET.getId());

        this.getOrCreateRawBuilder(ItemTags.CHEST_ARMOR)
                .addElement(Registration.CRYSTAL_CHESTPLATE.getId())
                .addElement(Registration.CRYSTAL_ELYTRA.getId());

        this.getOrCreateRawBuilder(ItemTags.LEG_ARMOR).addElement(
                Registration.CRYSTAL_LEGGINGS.getId());

        this.getOrCreateRawBuilder(ItemTags.FOOT_ARMOR).addElement(
                Registration.CRYSTAL_BOOTS.getId());

        this.getOrCreateRawBuilder(CrystalToolsTags.CRYSTAL_TOOL)
                .addElement(Registration.CRYSTAL_PICKAXE.getId())
                .addElement(Registration.CRYSTAL_AXE.getId())
                .addElement(Registration.CRYSTAL_HOE.getId())
                .addElement(Registration.CRYSTAL_SWORD.getId())
                .addElement(Registration.CRYSTAL_BOW.getId())
                .addElement(Registration.CRYSTAL_AIOT.getId())
                .addElement(Registration.CRYSTAL_ROCKET.getId())
                .addElement(Registration.CRYSTAL_TRIDENT.getId())
                .addElement(Registration.CRYSTAL_FISHING_ROD.getId())
                .addElement(Registration.CRYSTAL_SHIELD.getId())
                .addElement(Registration.CRYSTAL_HELMET.getId())
                .addElement(Registration.CRYSTAL_CHESTPLATE.getId())
                .addElement(Registration.CRYSTAL_LEGGINGS.getId())
                .addElement(Registration.CRYSTAL_BOOTS.getId())
                .addElement(Registration.CRYSTAL_ELYTRA.getId());
    }
}
