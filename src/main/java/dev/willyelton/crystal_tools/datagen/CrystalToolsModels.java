package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.client.renderer.CrystalShieldRenderer;
import dev.willyelton.crystal_tools.client.renderer.CrystalTridentSpecialRenderer;
import dev.willyelton.crystal_tools.client.renderer.item.properties.BowUseDuration;
import dev.willyelton.crystal_tools.common.levelable.armor.CrystalToolsArmorMaterials;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

import static dev.willyelton.crystal_tools.CrystalTools.rl;
import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_PREFIX_BOOTS;
import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_PREFIX_CHESTPLATE;
import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_PREFIX_HELMET;
import static net.minecraft.client.data.models.ItemModelGenerators.TRIM_PREFIX_LEGGINGS;

public class CrystalToolsModels extends ModelProvider {
    public CrystalToolsModels(PackOutput output) {
        super(output, CrystalTools.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Items
        itemModels.generateFlatItem(Registration.CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.NETHERITE_STICK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.NETHERITE_INFUSED_CRYSTAL_SHARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(Registration.CRYSTAL_UPGRADE_SMITHING_TEMPLATE.get(), ModelTemplates.FLAT_ITEM);
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
        generateCrystalBow(itemModels);
        generateCrystalTrident(itemModels);
        itemModels.generateFishingRod(Registration.CRYSTAL_FISHING_ROD.get());
        generateCrystalShield(itemModels);

        // Armor
        itemModels.generateTrimmableItem(Registration.CRYSTAL_HELMET.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, TRIM_PREFIX_HELMET, false);
        itemModels.generateTrimmableItem(Registration.CRYSTAL_CHESTPLATE.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, TRIM_PREFIX_CHESTPLATE, false);
        itemModels.generateTrimmableItem(Registration.CRYSTAL_LEGGINGS.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, TRIM_PREFIX_LEGGINGS, false);
        itemModels.generateTrimmableItem(Registration.CRYSTAL_BOOTS.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, TRIM_PREFIX_BOOTS, false);
        itemModels.generateFlatItem(Registration.CRYSTAL_ELYTRA.get(), ModelTemplates.FLAT_ITEM);

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

        blockModels.createFurnace(Registration.CRYSTAL_FURNACE.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFlatItemModel(Registration.CRYSTAL_FURNACE_ITEM.get());
        blockModels.createFurnace(Registration.CRYSTAL_GENERATOR.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFlatItemModel(Registration.CRYSTAL_GENERATOR_ITEM.get());
        blockModels.createFurnace(Registration.CRYSTAL_QUARRY.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFlatItemModel(Registration.CRYSTAL_QUARRY_ITEM.get());

        blockModels.createFlatItemModel(Registration.QUARRY_STABILIZER_ITEM.get());
    }

    private void generateCrystalBow(ItemModelGenerators itemModels) {
        Item bowItem = Registration.CRYSTAL_BOW.get();

        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(itemModels.createFlatItemModel(bowItem, CrystalToolsModelTemplates.CRYSTAL_BOW));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(bowItem, "_pulling_0", ModelTemplates.BOW));
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(bowItem, "_pulling_1", ModelTemplates.BOW));
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.plainModel(itemModels.createFlatItemModel(bowItem, "_pulling_2", ModelTemplates.BOW));
        itemModels.itemModelOutput
                .accept(
                        bowItem,
                        ItemModelUtils.conditional(
                                ItemModelUtils.isUsingItem(),
                                ItemModelUtils.rangeSelect(
                                        new BowUseDuration(),
                                        1F,
                                        itemmodel$unbaked1,
                                        ItemModelUtils.override(itemmodel$unbaked2, 0.65F),
                                        ItemModelUtils.override(itemmodel$unbaked3, 0.9F)
                                ),
                                itemmodel$unbaked
                        )
                );
    }

    private void generateCrystalTrident(ItemModelGenerators itemModels) {
        Item tridentItem = Registration.CRYSTAL_TRIDENT.get();

        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.plainModel(itemModels.createFlatItemModel(tridentItem, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.specialModel(
                CrystalToolsModelTemplates.CRYSTAL_TRIDENT_IN_HAND.create(tridentItem, TextureMapping.particle(rl("block/crystal_block")), itemModels.modelOutput),
                new CrystalTridentSpecialRenderer.Unbaked()
        );
        ItemModel.Unbaked itemmodel$unbaked2 = ItemModelUtils.specialModel(
                CrystalToolsModelTemplates.CRYSTAL_TRIDENT_THROWING.create(tridentItem, TextureMapping.particle(rl("block/crystal_block")), itemModels.modelOutput),
                new CrystalTridentSpecialRenderer.Unbaked()
        );
        ItemModel.Unbaked itemmodel$unbaked3 = ItemModelUtils.conditional(ItemModelUtils.isUsingItem(), itemmodel$unbaked2, itemmodel$unbaked1);
        itemModels.itemModelOutput.accept(tridentItem, ItemModelGenerators.createFlatModelDispatch(itemmodel$unbaked, itemmodel$unbaked3));
    }

    public void generateCrystalShield(ItemModelGenerators itemModels) {
        Item shieldItem = Registration.CRYSTAL_SHIELD.get();
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.specialModel(CrystalToolsModelTemplates.CRYSTAL_SHIELD.create(shieldItem, TextureMapping.particle(Registration.CRYSTAL_BLOCK.get()), itemModels.modelOutput),
                new CrystalShieldRenderer.Unbaked());
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.specialModel(
                CrystalToolsModelTemplates.CRYSTAL_SHIELD_BLOCKING.create(shieldItem, TextureMapping.particle(Registration.CRYSTAL_BLOCK.get()), itemModels.modelOutput),
                new CrystalShieldRenderer.Unbaked());
        itemModels.generateBooleanDispatch(Registration.CRYSTAL_SHIELD.get(), ItemModelUtils.isUsingItem(), itemmodel$unbaked1, itemmodel$unbaked);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return super.getKnownBlocks().filter(h -> !h.value().equals(Registration.QUARRY_STABILIZER.get()));
    }
}
