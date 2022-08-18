package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.block.ModBlocks;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class Items extends ItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, CrystalTools.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//        withExistingParent(ModBlocks.CRYSTAL_ORE_ITEM.get().getRegistryName().getPath(), new ResourceLocation(CrystalTools.MODID, "block/crystal_ore"));
    }
}
