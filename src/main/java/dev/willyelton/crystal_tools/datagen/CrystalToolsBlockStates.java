package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

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
        registerMachineBlock(Registration.CRYSTAL_FURNACE, "crystal_furnace");
        registerMachineBlock(Registration.CRYSTAL_GENERATOR, "crystal_generator");
        registerMachineBlock(Registration.CRYSTAL_QUARRY, "crystal_quarry");
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

    private void registerMachineBlock(DeferredHolder<Block, ?> holder, String name) {
        BlockModelBuilder offState = models().getBuilder(holder.getId().getPath())
                .parent(models().getExistingFile(mcLoc("orientable")))
                .texture("top", modLoc(String.format("block/%s_top", name)))
                .texture("front", modLoc(String.format("block/%s_front_off", name)))
                .texture("side", modLoc(String.format("block/%s_side", name)));

        BlockModelBuilder onState = models().getBuilder(holder.getId().getPath() + "_on")
                .parent(models().getExistingFile(mcLoc("orientable")))
                .texture("top", modLoc(String.format("block/%s_top", name)))
                .texture("front", modLoc(String.format("block/%s_front_on", name)))
                .texture("side", modLoc(String.format("block/%s_side", name)));

        horizontalBlock(holder.get(),
                blockState -> blockState.getValue(BlockStateProperties.LIT) ? onState : offState);
    }
}
