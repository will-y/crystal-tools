package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.client.model.property.Disabled;
import dev.willyelton.crystal_tools.client.model.property.FullCage;
import dev.willyelton.crystal_tools.client.model.property.Lit;
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
import net.minecraft.client.renderer.item.ConditionalItemModel;
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
        itemModels.generateFlatItem(ModRegistration.CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModRegistration.NETHERITE_STICK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModRegistration.NETHERITE_INFUSED_CRYSTAL_SHARD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_UPGRADE_SMITHING_TEMPLATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_APPLE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_BACKPACK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_COLLAR.get(), ModelTemplates.FLAT_ITEM);
        generateDogCage(itemModels);
        generateCrystalMagnet(itemModels);

        // Tools
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_AIOT.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_ROCKET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        generateCrystalBow(itemModels);
        generateCrystalTrident(itemModels);
        itemModels.generateFishingRod(ModRegistration.CRYSTAL_FISHING_ROD.get());
        generateCrystalShield(itemModels);
        generatePortableGenerator(itemModels);

        // Armor
        itemModels.generateTrimmableItem(ModRegistration.CRYSTAL_HELMET.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, TRIM_PREFIX_HELMET, false);
        itemModels.generateTrimmableItem(ModRegistration.CRYSTAL_CHESTPLATE.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, TRIM_PREFIX_CHESTPLATE, false);
        itemModels.generateTrimmableItem(ModRegistration.CRYSTAL_LEGGINGS.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, TRIM_PREFIX_LEGGINGS, false);
        itemModels.generateTrimmableItem(ModRegistration.CRYSTAL_BOOTS.get(), CrystalToolsArmorMaterials.CRYSTAL_EQUIPMENT_ASSET, TRIM_PREFIX_BOOTS, false);
        itemModels.generateFlatItem(ModRegistration.CRYSTAL_ELYTRA.get(), ModelTemplates.FLAT_ITEM);

        // Blocks
        blockModels.createTrivialCube(ModRegistration.CRYSTAL_BLOCK.get());
        blockModels.createFlatItemModel(ModRegistration.CRYSTAL_BLOCK_ITEM.get());
        blockModels.createTrivialCube(ModRegistration.CRYSTAL_GEODE.get());
        blockModels.createTrivialCube(ModRegistration.NETHERITE_INFUSED_CRYSTAL_GEODE.get());
        blockModels.createFlatItemModel(ModRegistration.CRYSTAL_GEODE_BLOCK_ITEM.get());
        blockModels.createFlatItemModel(ModRegistration.NETHERITE_INFUSED_CRYSTAL_GEODE_BLOCK_ITEM.get());
        blockModels.createTrivialCube(ModRegistration.CRYSTAL_ORE.get());
        blockModels.createFlatItemModel(ModRegistration.CRYSTAL_ORE_ITEM.get());
        blockModels.createTrivialCube(ModRegistration.CRYSTAL_DEEPSLATE_ORE.get());
        blockModels.createFlatItemModel(ModRegistration.CRYSTAL_DEEPSLATE_ORE_ITEM.get());

        blockModels.createNormalTorch(ModRegistration.CRYSTAL_TORCH.get(), ModRegistration.CRYSTAL_WALL_TORCH.get());

        blockModels.createFurnace(ModRegistration.CRYSTAL_FURNACE.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFlatItemModel(ModRegistration.CRYSTAL_FURNACE_ITEM.get());
        blockModels.createFurnace(ModRegistration.CRYSTAL_GENERATOR.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFlatItemModel(ModRegistration.CRYSTAL_GENERATOR_ITEM.get());
        blockModels.createFurnace(ModRegistration.CRYSTAL_QUARRY.get(), TexturedModel.ORIENTABLE_ONLY_TOP);
        blockModels.createFlatItemModel(ModRegistration.CRYSTAL_QUARRY_ITEM.get());

        blockModels.createFlatItemModel(ModRegistration.QUARRY_STABILIZER_ITEM.get());
        blockModels.createFlatItemModel(ModRegistration.CRYSTAL_PEDESTAL_ITEM.get());
    }

    private void generateCrystalBow(ItemModelGenerators itemModels) {
        Item bowItem = ModRegistration.CRYSTAL_BOW.get();

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
        Item tridentItem = ModRegistration.CRYSTAL_TRIDENT.get();

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

    private void generateCrystalShield(ItemModelGenerators itemModels) {
        Item shieldItem = ModRegistration.CRYSTAL_SHIELD.get();
        ItemModel.Unbaked itemmodel$unbaked = ItemModelUtils.specialModel(CrystalToolsModelTemplates.CRYSTAL_SHIELD.create(shieldItem, TextureMapping.particle(ModRegistration.CRYSTAL_BLOCK.get()), itemModels.modelOutput),
                new CrystalShieldRenderer.Unbaked());
        ItemModel.Unbaked itemmodel$unbaked1 = ItemModelUtils.specialModel(
                CrystalToolsModelTemplates.CRYSTAL_SHIELD_BLOCKING.create(shieldItem, TextureMapping.particle(ModRegistration.CRYSTAL_BLOCK.get()), itemModels.modelOutput),
                new CrystalShieldRenderer.Unbaked());
        itemModels.generateBooleanDispatch(ModRegistration.CRYSTAL_SHIELD.get(), ItemModelUtils.isUsingItem(), itemmodel$unbaked1, itemmodel$unbaked);
    }

    private void generateCrystalMagnet(ItemModelGenerators itemModels) {
        Item magnetItem = ModRegistration.CRYSTAL_MAGNET.get();
        ItemModel.Unbaked enabled = ItemModelUtils.plainModel(itemModels.createFlatItemModel(magnetItem, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked disabled = ItemModelUtils.plainModel(itemModels.createFlatItemModel(magnetItem, "_disabled", ModelTemplates.FLAT_ITEM));

        itemModels.itemModelOutput.accept(magnetItem, new ConditionalItemModel.Unbaked(
                new Disabled(),
                disabled,
                enabled));
    }

    private void generatePortableGenerator(ItemModelGenerators itemModels) {
        Item portableGenerator = ModRegistration.PORTABLE_GENERATOR.get();
        ItemModel.Unbaked unLit = ItemModelUtils.plainModel(itemModels.createFlatItemModel(portableGenerator, ModelTemplates.FLAT_HANDHELD_ITEM));
        ItemModel.Unbaked lit = ItemModelUtils.plainModel(itemModels.createFlatItemModel(portableGenerator, "_lit", ModelTemplates.FLAT_HANDHELD_ITEM));

        itemModels.itemModelOutput.accept(portableGenerator, new ConditionalItemModel.Unbaked(
                new Lit(),
                lit,
                unLit));
    }

    private void generateDogCage(ItemModelGenerators itemModels) {
        Item portableGenerator = ModRegistration.CRYSTAL_DOG_CAGE.get();
        ItemModel.Unbaked empty = ItemModelUtils.plainModel(itemModels.createFlatItemModel(portableGenerator, ModelTemplates.FLAT_HANDHELD_ITEM));
        ItemModel.Unbaked full = ItemModelUtils.plainModel(itemModels.createFlatItemModel(portableGenerator, "_full", ModelTemplates.FLAT_HANDHELD_ITEM));

        itemModels.itemModelOutput.accept(portableGenerator, new ConditionalItemModel.Unbaked(
                new FullCage(),
                full,
                empty));
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return super.getKnownBlocks().filter(h -> !h.value().equals(ModRegistration.QUARRY_STABILIZER.get()) &&
                !h.value().equals(ModRegistration.CRYSTAL_PEDESTAL.get()));
    }
}
