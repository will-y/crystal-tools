package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.block.ModBlocks;
import dev.willyelton.crystal_tools.CrystalTools;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, CrystalTools.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.CRYSTAL_ORE.get());
    }
}
