package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CrystalToolsItemModels extends ItemModelProvider {
    public CrystalToolsItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CrystalTools.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Blocks
        withExistingParent(Registration.CRYSTAL_BLOCK.getId().getPath(), modLoc("block/crystal_block"));
        withExistingParent(Registration.CRYSTAL_ORE.getId().getPath(), modLoc("block/crystal_ore"));
        withExistingParent(Registration.CRYSTAL_DEEPSLATE_ORE.getId().getPath(), modLoc("block/crystal_deepslate_ore"));
        withExistingParent(Registration.CRYSTAL_FURNACE.getId().getPath(), modLoc("block/crystal_furnace"));
        withExistingParent(Registration.CRYSTAL_TORCH.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("block/crystal_torch"));

        // Items
        basicItem(Registration.CRYSTAL.get());
        basicItem(Registration.NETHERITE_STICK.get());
        basicItem(Registration.CRYSTAL_APPLE.get());
        basicItem(Registration.CRYSTAL_BACKPACK.get());

        // Tools
        basicItem(Registration.CRYSTAL_AIOT.get());
        basicItem(Registration.CRYSTAL_AXE.get());
        basicItem(Registration.CRYSTAL_HOE.get());
        basicItem(Registration.CRYSTAL_PICKAXE.get());
        basicItem(Registration.CRYSTAL_ROCKET.get());
        basicItem(Registration.CRYSTAL_SHOVEL.get());
        basicItem(Registration.CRYSTAL_SWORD.get());
        registerBow();
        registerTrident();

        // Armor
        basicItem(Registration.CRYSTAL_HELMET.get());
        basicItem(Registration.CRYSTAL_CHESTPLATE.get());
        basicItem(Registration.CRYSTAL_LEGGINGS.get());
        basicItem(Registration.CRYSTAL_BOOTS.get());
        basicItem(Registration.CRYSTAL_ELYTRA.get());
    }

    private void registerBow() {
        ModelFile[] bowModels = new ModelFile[3];

        for (int i = 0; i < 3; i++) {
            ResourceLocation resourceLocation = new ResourceLocation(CrystalTools.MODID, "item/crystal_bow_pulling_" + i);
            bowModels[i] = getBuilder(resourceLocation.toString())
                    .parent(new ModelFile.UncheckedModelFile(CrystalTools.MODID + ":item/crystal_bow"))
                    .texture("layer0", resourceLocation);
        }

        basicItem(Registration.CRYSTAL_BOW.get())
                .transforms()
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                        .rotation(-80, 260, -40)
                        .translation(-1F, -2F, 2.5F)
                        .scale(0.9F, 0.9F, 0.9F)
                    .end()
                    .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                        .rotation(-80, -280, 40)
                        .translation(-1F, -2F, 2.5F)
                        .scale(0.9F, 0.9F, 0.9F)
                    .end()
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                        .rotation(0, -90, 25)
                        .translation(1.13F, 3.2F, 1.13F)
                        .scale(0.68F, 0.68F, 0.68F)
                    .end()
                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                        .rotation(0, 90, -25)
                        .translation(1.13F, 3.2F, 1.13F)
                        .scale(0.68F, 0.68F, 0.68F)
                    .end()
                .end()
                .override()
                    .predicate(mcLoc("pulling"), 1)
                    .model(bowModels[0])
                .end()
                .override()
                    .predicate(mcLoc("pulling"), 1)
                    .predicate(mcLoc("pull"), 0.65F)
                    .model(bowModels[1])
                .end()
                .override()
                    .predicate(mcLoc("pulling"), 1)
                    .predicate(mcLoc("pull"), 0.9F)
                    .model(bowModels[2])
                .end();
    }

    private void registerTrident() {
        // Inventory Model
        ResourceLocation tridentLocation = Registration.CRYSTAL_TRIDENT.getId();
        ResourceLocation tridentTextureLocation = new ResourceLocation(tridentLocation.getNamespace(), "item/" + tridentLocation.getPath());
        getBuilder(tridentLocation + "_inventory")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", tridentTextureLocation);

        // Throwing Model
        ModelFile throwingModel = getBuilder(tridentLocation + "_throwing")
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .guiLight(BlockModel.GuiLight.FRONT)
                .texture("particle", tridentTextureLocation)
                .transforms()
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                        .rotation(0, 90, 180)
                        .translation(8, -17, 9)
                        .scale(1, 1, 1)
                    .end()
                    .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                        .rotation(0, 90, 180)
                        .translation(8, -17, -7)
                        .scale(1, 1, 1)
                    .end()
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                        .rotation(0, -90, 25)
                        .translation(-3, 17, 1)
                        .scale(1, 1, 1)
                    .end()
                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                        .rotation(0, 90, -25)
                        .translation(13, 17, 1)
                        .scale(1, 1, 1)
                    .end()
                    .transform(ItemDisplayContext.GUI)
                        .rotation(15, -25, -5)
                        .translation(2, 3, 0)
                        .scale(0.65F, 0.65F, 0.65F)
                    .end()
                    .transform(ItemDisplayContext.FIXED)
                        .rotation(0, 180, 0)
                        .translation(-2, 4, -5)
                        .scale(0.5F, 0.5F, 0.5F)
                    .end()
                    .transform(ItemDisplayContext.GROUND)
                        .rotation(0, 0, 0)
                        .translation(4, 4, 2)
                        .scale(0.25F, 0.25F, 0.25F)
                    .end()
                .end();

        // Normal Model
        getBuilder(tridentLocation.toString())
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .guiLight(BlockModel.GuiLight.FRONT)
                .texture("particle", tridentTextureLocation)
                .transforms()
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                        .rotation(0, 60, 0)
                        .translation(11, 17, -2)
                        .scale(1, 1, 1)
                    .end()
                    .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                        .rotation(0, 60, 0)
                        .translation(3, 17, 12)
                        .scale(1, 1, 1)
                    .end()
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                        .rotation(0, -90, 25)
                        .translation(-3, 17, 1)
                        .scale(1, 1, 1)
                    .end()
                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                        .rotation(0, 90, -25)
                        .translation(13, 17 , 1)
                        .scale(1, 1, 1)
                    .end()
                    .transform(ItemDisplayContext.GUI)
                        .rotation(15, -25, -5)
                        .translation(2, 3, 0)
                        .scale(0.65F, 0.65F, 0.65F)
                    .end()
                    .transform(ItemDisplayContext.FIXED)
                        .rotation(0, 180, 0)
                        .translation(-2, 4, -5)
                        .scale(0.5F, 0.5F, 0.5F)
                    .end()
                    .transform(ItemDisplayContext.GROUND)
                        .rotation(0, 0, 0)
                        .translation(4, 4, 2)
                        .scale(0.25F, 0.25F, 0.25F)
                    .end()
                .end()
                .override()
                    .predicate(mcLoc("throwing"), 1)
                    .model(throwingModel)
                .end();
    }
}
