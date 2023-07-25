package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
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

        // Tools
        basicItem(Registration.CRYSTAL_AIOT.get());
        basicItem(Registration.CRYSTAL_AXE.get());
        basicItem(Registration.CRYSTAL_HOE.get());
        basicItem(Registration.CRYSTAL_PICKAXE.get());
        basicItem(Registration.CRYSTAL_ROCKET.get());
        basicItem(Registration.CRYSTAL_SHOVEL.get());
        basicItem(Registration.CRYSTAL_SWORD.get());
        registerBow();

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
}
