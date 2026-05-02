package dev.willyelton.crystal.tools.datagen;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.tools.CrystalTools;
import dev.willyelton.crystal.tools.ModRegistration;
import dev.willyelton.crystal.tools.common.tags.CrystalToolsTags;
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

        this.getOrCreateRawBuilder(ItemTags.PICKAXES)
                .addElement(ModRegistration.CRYSTAL_PICKAXE.getId())
                .addElement(ModRegistration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.AXES)
                .addElement(ModRegistration.CRYSTAL_AXE.getId())
                .addElement(ModRegistration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.SHOVELS)
                .addElement(ModRegistration.CRYSTAL_SHOVEL.getId())
                .addElement(ModRegistration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.HOES)
                .addElement(ModRegistration.CRYSTAL_HOE.getId())
                .addElement(ModRegistration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.SWORDS)
                .addElement(ModRegistration.CRYSTAL_SWORD.getId())
                .addElement(ModRegistration.CRYSTAL_AIOT.getId());

        this.getOrCreateRawBuilder(ItemTags.BOW_ENCHANTABLE).addElement(
                ModRegistration.CRYSTAL_BOW.getId());

        this.getOrCreateRawBuilder(ItemTags.TRIDENT_ENCHANTABLE).addElement(
                ModRegistration.CRYSTAL_TRIDENT.getId());

        this.getOrCreateRawBuilder(Tags.Items.TOOLS_TRIDENT).addElement(
                ModRegistration.CRYSTAL_TRIDENT.getId());

        this.getOrCreateRawBuilder(Tags.Items.TOOLS_FISHING_ROD).addElement(
                ModRegistration.CRYSTAL_FISHING_ROD.getId());

        this.getOrCreateRawBuilder(ItemTags.HEAD_ARMOR).addElement(
                ModRegistration.CRYSTAL_HELMET.getId());

        this.getOrCreateRawBuilder(ItemTags.CHEST_ARMOR)
                .addElement(ModRegistration.CRYSTAL_CHESTPLATE.getId())
                .addElement(ModRegistration.CRYSTAL_ELYTRA.getId());

        this.getOrCreateRawBuilder(ItemTags.LEG_ARMOR).addElement(
                ModRegistration.CRYSTAL_LEGGINGS.getId());

        this.getOrCreateRawBuilder(ItemTags.FOOT_ARMOR).addElement(
                ModRegistration.CRYSTAL_BOOTS.getId());

        this.getOrCreateRawBuilder(CrystalToolsTags.CRYSTAL_TOOL)
                .addElement(ModRegistration.CRYSTAL_PICKAXE.getId())
                .addElement(ModRegistration.CRYSTAL_AXE.getId())
                .addElement(ModRegistration.CRYSTAL_HOE.getId())
                .addElement(ModRegistration.CRYSTAL_SWORD.getId())
                .addElement(ModRegistration.CRYSTAL_BOW.getId())
                .addElement(ModRegistration.CRYSTAL_AIOT.getId())
                .addElement(ModRegistration.CRYSTAL_ROCKET.getId())
                .addElement(ModRegistration.CRYSTAL_TRIDENT.getId())
                .addElement(ModRegistration.CRYSTAL_FISHING_ROD.getId())
                .addElement(ModRegistration.CRYSTAL_SHIELD.getId())
                .addElement(ModRegistration.CRYSTAL_HELMET.getId())
                .addElement(ModRegistration.CRYSTAL_CHESTPLATE.getId())
                .addElement(ModRegistration.CRYSTAL_LEGGINGS.getId())
                .addElement(ModRegistration.CRYSTAL_BOOTS.getId())
                .addElement(ModRegistration.CRYSTAL_ELYTRA.getId());
    }
}
