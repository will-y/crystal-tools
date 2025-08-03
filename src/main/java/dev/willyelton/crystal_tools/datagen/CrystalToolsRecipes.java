package dev.willyelton.crystal_tools.datagen;

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
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.NotCondition;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.data.recipes.ShapedRecipeBuilder.shaped;

public class CrystalToolsRecipes extends RecipeProvider {
    private static final Criterion<InventoryChangeTrigger.TriggerInstance> HAS_CRYSTAL = InventoryChangeTrigger.TriggerInstance.hasItems(Registration.CRYSTAL.get());
    public CrystalToolsRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Basic
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Registration.CRYSTAL.get(), 9)
                .requires(Registration.CRYSTAL_BLOCK.get())
                .unlockedBy("has_crystal_block",
                        InventoryChangeTrigger.TriggerInstance.hasItems(Registration.CRYSTAL_BLOCK.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.DECORATIONS, Registration.CRYSTAL_BLOCK.get())
                .pattern("ccc")
                .pattern("ccc")
                .pattern("ccc")
                .define('c', Registration.CRYSTAL.get())
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(recipeOutput);

        shaped(RecipeCategory.MISC, Registration.NETHERITE_STICK.get(), 4)
                .pattern("n")
                .pattern("n")
                .define('n', Tags.Items.INGOTS_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(recipeOutput);

        shaped(RecipeCategory.MISC, Registration.CRYSTAL_TORCH.get(), 4)
                .pattern("c")
                .pattern("s")
                .define('c', Registration.CRYSTAL.get())
                .define('s', Items.STICK)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(recipeOutput);

        // Tools
        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_AXE.get())
                    .pattern("cc")
                    .pattern("cs")
                    .pattern(" s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_AXE.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_HOE.get())
                    .pattern("cc")
                    .pattern(" s")
                    .pattern(" s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_HOE.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_PICKAXE.get())
                    .pattern("ccc")
                    .pattern(" s ")
                    .pattern(" s ")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_PICKAXE.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_SHOVEL.get())
                    .pattern("c")
                    .pattern("s")
                    .pattern("s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_SHOVEL.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.COMBAT, Registration.CRYSTAL_SWORD.get())
                    .pattern("c")
                    .pattern("c")
                    .pattern("s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_SWORD.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.COMBAT, Registration.CRYSTAL_BOW.get())
                    .pattern(" sc")
                    .pattern("s c")
                    .pattern(" sc")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_BOW.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.FOOD, Registration.CRYSTAL_APPLE.get())
                        .pattern("ccc")
                        .pattern("cac")
                        .pattern("ccc")
                        .define('c', Registration.CRYSTAL.get())
                        .define('a', Items.ENCHANTED_GOLDEN_APPLE)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_APPLE.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_ROCKET.get())
                        .pattern("ccc")
                        .pattern("crc")
                        .pattern("ccc")
                        .define('c', Registration.CRYSTAL.get())
                        .define('r', Items.FIREWORK_ROCKET)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_ROCKET.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_TRIDENT.get())
                        .pattern("ccc")
                        .pattern("sts")
                        .pattern(" s ")
                        .define('c', Registration.CRYSTAL.get())
                        .define('t', Items.TRIDENT)
                        .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_TRIDENT.getId());

        buildConditionalRecipe(recipeOutput,
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
                .save(recipeOutput);

        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_MAGNET.get())
                .pattern(" cr")
                .pattern("cic")
                .pattern("rc ")
                .define('c', Registration.CRYSTAL.get())
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(recipeOutput);

        // Armor
        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_HELMET.get())
                        .pattern("ccc")
                        .pattern("c c")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_HELMET.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_CHESTPLATE.get())
                        .pattern("c c")
                        .pattern("ccc")
                        .pattern("ccc")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_CHESTPLATE.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_LEGGINGS.get())
                        .pattern("ccc")
                        .pattern("c c")
                        .pattern("c c")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_LEGGINGS.getId());

        buildConditionalRecipe(recipeOutput,
                shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_BOOTS.get())
                        .pattern("c c")
                        .pattern("c c")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_BOOTS.getId());

        buildConditionalRecipe(recipeOutput,
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
                .save(recipeOutput);

        shaped(RecipeCategory.MISC, Registration.QUARRY_STABILIZER.get(), 4)
                .pattern("c")
                .pattern("n")
                .define('c', Registration.CRYSTAL.get())
                .define('n', Items.NETHERITE_INGOT)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(recipeOutput);

        shaped(RecipeCategory.MISC, Registration.CRYSTAL_PEDESTAL.get())
                .pattern("n")
                .pattern("b")
                .pattern("c")
                .define('n', Items.NETHERITE_INGOT)
                .define('b', Registration.CRYSTAL_BLOCK.get())
                .define('c', Items.CHEST)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        // Special
        SpecialRecipeBuilder
                .special(CrystalAIOTRecipe::new)
                .save(recipeOutput, Registration.CRYSTAL_AIOT.getId().toString());

        SpecialRecipeBuilder
                .special(CrystalElytraRecipe::new)
                .save(recipeOutput, Registration.CRYSTAL_ELYTRA.getId().toString());

        SpecialRecipeBuilder
                .special(CrystalGeneratorRecipe::new)
                .save(recipeOutput, Registration.CRYSTAL_GENERATOR_ITEM.getId().toString());

        SpecialRecipeBuilder
                .special(CrystalQuarryRecipe::new)
                .save(recipeOutput, Registration.CRYSTAL_QUARRY_ITEM.getId().toString());

        SpecialRecipeBuilder
                .special(CrystalShieldTotemRecipe::new)
                .save(recipeOutput, Registration.CRYSTAL_SHIELD.getId() + "_totem");
    }

    private void buildConditionalRecipe(RecipeOutput recipeOutput, ShapedRecipeBuilder builder, ResourceLocation conditionItem) {
        builder.save(recipeOutput.withConditions(new NotCondition(new ItemDisabledCondition(conditionItem))), conditionItem);
    }
}
