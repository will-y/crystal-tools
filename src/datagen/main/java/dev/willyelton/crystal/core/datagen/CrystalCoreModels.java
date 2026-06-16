package dev.willyelton.crystal.core.datagen;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class CrystalCoreModels extends ModelProvider  {
    public CrystalCoreModels(PackOutput packOutput) {
        super(packOutput, ApiConstants.CORE_MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Items
        itemModels.generateFlatItem(Registration.CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.NETHERITE_STICK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.NETHERITE_INFUSED_CRYSTAL_SHARD.get(), ModelTemplates.FLAT_ITEM);

        // Blocks
        blockModels.createTrivialCube(Registration.CRYSTAL_BLOCK.get());
        blockModels.createFlatItemModel(Registration.CRYSTAL_BLOCK_ITEM.get());
        blockModels.createTrivialCube(Registration.CRYSTAL_GEODE.get());
        blockModels.createTrivialCube(Registration.NETHERITE_INFUSED_CRYSTAL_GEODE.get());
        blockModels.createFlatItemModel(Registration.CRYSTAL_GEODE_BLOCK_ITEM.get());
        blockModels.createFlatItemModel(Registration.NETHERITE_INFUSED_CRYSTAL_GEODE_BLOCK_ITEM.get());
        blockModels.createTrivialCube(Registration.CRYSTAL_ORE.get());
        blockModels.createFlatItemModel(Registration.CRYSTAL_ORE_ITEM.get());
        blockModels.createTrivialCube(Registration.CRYSTAL_DEEPSLATE_ORE.get());
        blockModels.createFlatItemModel(Registration.CRYSTAL_DEEPSLATE_ORE_ITEM.get());
        blockModels.createNormalTorch(Registration.CRYSTAL_TORCH.get(), Registration.CRYSTAL_WALL_TORCH.get());
    }
}
