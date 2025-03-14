package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.crafting.CrystalAIOTRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalElytraRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalGeneratorRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalQuarryRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalShieldTotemRecipe;
import dev.willyelton.crystal_tools.common.crafting.ItemDisabledCondition;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.NotCondition;

import java.util.concurrent.CompletableFuture;

public class CrystalToolsRecipes extends RecipeProvider {
    private static final Criterion<InventoryChangeTrigger.TriggerInstance> HAS_CRYSTAL = InventoryChangeTrigger.TriggerInstance.hasItems(Registration.CRYSTAL.get());
    public CrystalToolsRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        // Basic
        shapeless(RecipeCategory.MISC, Registration.CRYSTAL.get(), 9)
                .requires(Registration.CRYSTAL_BLOCK.get())
                .unlockedBy("has_crystal_block",
                        InventoryChangeTrigger.TriggerInstance.hasItems(Registration.CRYSTAL_BLOCK.get()))
                .save(output);

        shaped(RecipeCategory.DECORATIONS, Registration.CRYSTAL_BLOCK.get())
                .pattern("ccc")
                .pattern("ccc")
                .pattern("ccc")
                .define('c', Registration.CRYSTAL.get())
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.MISC, Registration.NETHERITE_STICK.get(), 4)
                .pattern("n")
                .pattern("n")
                .define('n', Tags.Items.INGOTS_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.MISC, Registration.CRYSTAL_TORCH.get(), 4)
                .pattern("c")
                .pattern("s")
                .define('c', Registration.CRYSTAL.get())
                .define('s', Items.STICK)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        // Tools
        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_AXE.get())
                    .pattern("cc")
                    .pattern("cs")
                    .pattern(" s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_AXE.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_HOE.get())
                    .pattern("cc")
                    .pattern(" s")
                    .pattern(" s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_HOE.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_PICKAXE.get())
                    .pattern("ccc")
                    .pattern(" s ")
                    .pattern(" s ")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_PICKAXE.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_SHOVEL.get())
                    .pattern("c")
                    .pattern("s")
                    .pattern("s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_SHOVEL.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.COMBAT, Registration.CRYSTAL_SWORD.get())
                    .pattern("c")
                    .pattern("c")
                    .pattern("s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_SWORD.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.COMBAT, Registration.CRYSTAL_BOW.get())
                    .pattern(" sc")
                    .pattern("s c")
                    .pattern(" sc")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_BOW.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.FOOD, Registration.CRYSTAL_APPLE.get())
                        .pattern("ccc")
                        .pattern("cac")
                        .pattern("ccc")
                        .define('c', Registration.CRYSTAL.get())
                        .define('a', Items.ENCHANTED_GOLDEN_APPLE)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_APPLE.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_ROCKET.get())
                        .pattern("ccc")
                        .pattern("crc")
                        .pattern("ccc")
                        .define('c', Registration.CRYSTAL.get())
                        .define('r', Items.FIREWORK_ROCKET)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_ROCKET.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_TRIDENT.get())
                        .pattern("ccc")
                        .pattern("sts")
                        .pattern(" s ")
                        .define('c', Registration.CRYSTAL.get())
                        .define('t', Items.TRIDENT)
                        .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_TRIDENT.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_FISHING_ROD.get())
                        .pattern("  s")
                        .pattern(" st")
                        .pattern("s c")
                        .define('c', Registration.CRYSTAL.get())
                        .define('t', Items.STRING)
                        .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_FISHING_ROD.getId());

        shaped(RecipeCategory.MISC, Registration.CRYSTAL_SHIELD.get())
                .pattern(" c ")
                .pattern("tsc")
                .pattern(" c ")
                .define('c', Registration.CRYSTAL.get())
                .define('t', Registration.NETHERITE_STICK.get())
                .define('s', Items.SHIELD)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        // Armor
        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_HELMET.get())
                        .pattern("ccc")
                        .pattern("c c")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_HELMET.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_CHESTPLATE.get())
                        .pattern("c c")
                        .pattern("ccc")
                        .pattern("ccc")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_CHESTPLATE.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_LEGGINGS.get())
                        .pattern("ccc")
                        .pattern("c c")
                        .pattern("c c")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_LEGGINGS.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_BOOTS.get())
                        .pattern("c c")
                        .pattern("c c")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_BOOTS.getId());

        buildConditionalRecipe(output,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_BACKPACK.get())
                        .pattern("lcl")
                        .pattern("chc")
                        .pattern("lcl")
                        .define('l', Items.LEATHER)
                        .define('c', Registration.CRYSTAL.get())
                        .define('h', Items.CHEST)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_BACKPACK.getId());

        // Machines
        shaped(RecipeCategory.MISC, Registration.CRYSTAL_FURNACE.get())
                .pattern("ccc")
                .pattern("cfc")
                .pattern("ccc")
                .define('c', Registration.CRYSTAL.get())
                .define('f', Items.FURNACE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.MISC, Registration.QUARRY_STABILIZER.get(), 4)
                .pattern("c")
                .pattern("n")
                .define('c', Registration.CRYSTAL.get())
                .define('n', Items.NETHERITE_INGOT)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        // Special
        SpecialRecipeBuilder
                .special(CrystalAIOTRecipe::new)
                .save(output, Registration.CRYSTAL_AIOT.getId().toString());

        SpecialRecipeBuilder
                .special(CrystalElytraRecipe::new)
                .save(output, Registration.CRYSTAL_ELYTRA.getId().toString());

        SpecialRecipeBuilder
                .special(CrystalGeneratorRecipe::new)
                .save(output, Registration.CRYSTAL_GENERATOR_ITEM.getId().toString());

        SpecialRecipeBuilder
                .special(CrystalQuarryRecipe::new)
                .save(output, Registration.CRYSTAL_QUARRY_ITEM.getId().toString());

        SpecialRecipeBuilder
                .special(CrystalShieldTotemRecipe::new)
                .save(output, Registration.CRYSTAL_SHIELD.getId() + "_totem");
    }

    private void buildConditionalRecipe(RecipeOutput output, ShapedRecipeBuilder builder, ResourceLocation conditionItem) {
        builder.save(output.withConditions(new NotCondition(new ItemDisabledCondition(conditionItem))), ResourceKey.create(Registries.RECIPE, conditionItem));
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
            return new CrystalToolsRecipes(provider, output);
        }

        @Override
        public String getName() {
            return CrystalTools.MODID + ":recipes";
        }
    }
}
