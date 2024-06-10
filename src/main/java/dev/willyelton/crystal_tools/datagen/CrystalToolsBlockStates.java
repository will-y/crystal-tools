package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.levelable.block.CrystalFurnaceBlock;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class CrystalToolsBlockStates extends BlockStateProvider {

    public CrystalToolsBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CrystalTools.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(Registration.CRYSTAL_BLOCK.get());
        simpleBlock(Registration.CRYSTAL_ORE.get());
        simpleBlock(Registration.CRYSTAL_DEEPSLATE_ORE.get());

        registerCrystalTorch();
        registerCrystalWallTorch();
        registerCrystalFurnace();
    }

    private void registerCrystalTorch() {
        BlockModelBuilder crystalTorchModel = models().getBuilder(Registration.CRYSTAL_TORCH.getId().getPath())
                .parent(models().getExistingFile(mcLoc("block/template_torch")))
                .renderType("cutout")
                .texture("torch", modLoc("block/crystal_torch"));
        simpleBlock(Registration.CRYSTAL_TORCH.get(), crystalTorchModel);
    }

    private void registerCrystalWallTorch() {
        BlockModelBuilder crystalTorchModel = models().getBuilder(Registration.CRYSTAL_WALL_TORCH.getId().getPath())
                .parent(models().getExistingFile(mcLoc("block/template_torch_wall")))
                .renderType("cutout")
                .texture("torch", modLoc("block/crystal_torch"));

        horizontalBlock(Registration.CRYSTAL_WALL_TORCH.get(), crystalTorchModel, 90);
    }

    private void registerCrystalFurnace() {
        BlockModelBuilder crystaLFurnaceOff = models().getBuilder(Registration.CRYSTAL_FURNACE.getId().getPath())
                .parent(models().getExistingFile(mcLoc("orientable")))
                .texture("top", modLoc("block/crystal_furnace_top"))
                .texture("front", modLoc("block/crystal_furnace_front_off"))
                .texture("side", modLoc("block/crystal_furnace_side"));

        BlockModelBuilder crystaLFurnaceOn = models().getBuilder(Registration.CRYSTAL_FURNACE.getId().getPath() + "_on")
                .parent(models().getExistingFile(mcLoc("orientable")))
                .texture("top", modLoc("block/crystal_furnace_top"))
                .texture("front", modLoc("block/crystal_furnace_front_on"))
                .texture("side", modLoc("block/crystal_furnace_side"));

        horizontalBlock(Registration.CRYSTAL_FURNACE.get(),
                blockState -> blockState.getValue(CrystalFurnaceBlock.LIT) ? crystaLFurnaceOn : crystaLFurnaceOff);
    }
}
