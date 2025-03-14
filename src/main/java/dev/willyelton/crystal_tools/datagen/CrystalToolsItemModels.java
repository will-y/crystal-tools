package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.levelable.armor.CrystalToolsArmorMaterials;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.TridentSpecialRenderer;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class CrystalToolsItemModels extends ModelProvider {
    public CrystalToolsItemModels(PackOutput output) {
        super(output, CrystalTools.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Items
        itemModels.generateFlatItem(Registration.CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.NETHERITE_STICK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.CRYSTAL_APPLE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.CRYSTAL_BACKPACK.get(), ModelTemplates.FLAT_ITEM);

        // Tools
        itemModels.generateFlatItem(Registration.CRYSTAL_AIOT.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(Registration.CRYSTAL_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(Registration.CRYSTAL_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(Registration.CRYSTAL_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(Registration.CRYSTAL_ROCKET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.CRYSTAL_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(Registration.CRYSTAL_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        // TODO: Will most likely have to do something about this, looks like the speed is hard coded into the model now ...
        itemModels.generateBow(Registration.CRYSTAL_BOW.get());
        generateCrystalTrident(itemModels);
        itemModels.generateFishingRod(Registration.CRYSTAL_FISHING_ROD.get());

        // Armor
        itemModels.generateTrimmableItem(Registration.CRYSTAL_HELMET.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, "helmet", false);
        itemModels.generateTrimmableItem(Registration.CRYSTAL_CHESTPLATE.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, "chestplate", false);
        itemModels.generateTrimmableItem(Registration.CRYSTAL_LEGGINGS.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, "leggings", false);
        itemModels.generateTrimmableItem(Registration.CRYSTAL_BOOTS.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, "boots", false);
        itemModels.generateFlatItem(Registration.CRYSTAL_ELYTRA.get(), ModelTemplates.FLAT_ITEM);

        // Blocks
        blockModels.createTrivialCube(Registration.CRYSTAL_BLOCK.get());
        blockModels.createFlatItemModel(Registration.CRYSTAL_BLOCK_ITEM.get());
        blockModels.createTrivialCube(Registration.CRYSTAL_ORE.get());
        blockModels.createFlatItemModel(Registration.CRYSTAL_ORE_ITEM.get());
        blockModels.createTrivialCube(Registration.CRYSTAL_DEEPSLATE_ORE.get());
        blockModels.createFlatItemModel(Registration.CRYSTAL_DEEPSLATE_ORE_ITEM.get());

        blockModels.createNormalTorch(Registration.CRYSTAL_TORCH.get(), Registration.CRYSTAL_WALL_TORCH.get());

        blockModels.createFurnace(Registration.CRYSTAL_FURNACE.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFlatItemModel(Registration.CRYSTAL_FURNACE_ITEM.get());
        blockModels.createFurnace(Registration.CRYSTAL_GENERATOR.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFlatItemModel(Registration.CRYSTAL_GENERATOR_ITEM.get());
        blockModels.createFurnace(Registration.CRYSTAL_QUARRY.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFlatItemModel(Registration.CRYSTAL_QUARRY_ITEM.get());

        blockModels.createFlatItemModel(Registration.QUARRY_STABILIZER_ITEM.get());
    }

    private void generateCrystalTrident(ItemModelGenerators itemModels) {
        Item tridentItem = Registration.CRYSTAL_TRIDENT.get();

        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(itemModels.createFlatItemModel(tridentItem, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.specialModel(
                ModelLocationUtils.getModelLocation(tridentItem, "_in_hand"), new TridentSpecialRenderer.Unbaked()
        );
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.specialModel(
                ModelLocationUtils.getModelLocation(tridentItem, "_throwing"), new TridentSpecialRenderer.Unbaked()
        );
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), itemmodel$unbaked2, itemmodel$unbaked1);
        itemModels.itemModelOutput.accept(tridentItem, ItemModelGenerators.createFlatModelDispatch(itemmodel$unbaked, itemmodel$unbaked3));
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return super.getKnownBlocks().filter(h -> !h.value().equals(Registration.QUARRY_STABILIZER.get()));
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        // TODO: Can probably datagen this pretty easily
        return super.getKnownItems().filter(h -> !h.value().equals(Registration.CRYSTAL_SHIELD.get()));
    }
}
