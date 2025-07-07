package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.crafting.CrystalAIOTRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalElytraRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalGeneratorRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalQuarryRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalShieldTotemRecipe;
import dev.willyelton.crystal_tools.common.crafting.CrystalSmithingRecipe;
import dev.willyelton.crystal_tools.common.tags.CrystalToolsTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;
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

        shaped(RecipeCategory.MISC, Registration.CRYSTAL_UPGRADE_SMITHING_TEMPLATE.get())
                .pattern("iii")
                .pattern("ici")
                .pattern("ini")
                .define('i', Registration.NETHERITE_INFUSED_CRYSTAL_SHARD.get())
                .define('n', Items.NETHERITE_INGOT)
                .define('c', Registration.CRYSTAL.get())
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shapeless(RecipeCategory.MISC, Registration.CRYSTAL_UPGRADE_SMITHING_TEMPLATE.get(), 2)
                .requires(Registration.CRYSTAL.get())
                .requires(Registration.CRYSTAL_UPGRADE_SMITHING_TEMPLATE.get())
                .requires(Items.NETHERITE_INGOT)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output, Registration.CRYSTAL_UPGRADE_SMITHING_TEMPLATE.getId() + "_dupe");

        // Tools
        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_AXE.get())
                .pattern("cc")
                .pattern("cs")
                .pattern(" s")
                .define('c', Registration.CRYSTAL.get())
                .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_HOE.get())
                .pattern("cc")
                .pattern(" s")
                .pattern(" s")
                .define('c', Registration.CRYSTAL.get())
                .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_PICKAXE.get())
                .pattern("ccc")
                .pattern(" s ")
                .pattern(" s ")
                .define('c', Registration.CRYSTAL.get())
                .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_SHOVEL.get())
                .pattern("c")
                .pattern("s")
                .pattern("s")
                .define('c', Registration.CRYSTAL.get())
                .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.COMBAT, Registration.CRYSTAL_SWORD.get())
                .pattern("c")
                .pattern("c")
                .pattern("s")
                .define('c', Registration.CRYSTAL.get())
                .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.COMBAT, Registration.CRYSTAL_BOW.get())
                .pattern(" sc")
                .pattern("s c")
                .pattern(" sc")
                .define('c', Registration.CRYSTAL.get())
                .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.FOOD, Registration.CRYSTAL_APPLE.get())
                .pattern("ccc")
                .pattern("cac")
                .pattern("ccc")
                .define('c', Registration.CRYSTAL.get())
                .define('a', Items.ENCHANTED_GOLDEN_APPLE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_ROCKET.get())
                .pattern("ccc")
                .pattern("crc")
                .pattern("ccc")
                .define('c', Registration.CRYSTAL.get())
                .define('r', Items.FIREWORK_ROCKET)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_TRIDENT.get())
                .pattern("ccc")
                .pattern("sts")
                .pattern(" s ")
                .define('c', Registration.CRYSTAL.get())
                .define('t', Items.TRIDENT)
                .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_FISHING_ROD.get())
                .pattern("  s")
                .pattern(" st")
                .pattern("s c")
                .define('c', Registration.CRYSTAL.get())
                .define('t', Items.STRING)
                .define('s', CrystalToolsTags.RODS_METAL_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.MISC, Registration.CRYSTAL_SHIELD.get())
                .pattern(" c ")
                .pattern("tsc")
                .pattern(" c ")
                .define('c', Registration.CRYSTAL.get())
                .define('t', Registration.NETHERITE_STICK.get())
                .define('s', Items.SHIELD)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_BACKPACK.get())
                .pattern("lcl")
                .pattern("chc")
                .pattern("lcl")
                .define('l', Items.LEATHER)
                .define('c', Registration.CRYSTAL.get())
                .define('h', Items.CHEST)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

        shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_MAGNET.get())
                .pattern(" cr")
                .pattern("cic")
                .pattern("rc ")
                .define('c', Registration.CRYSTAL.get())
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(output);

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

        crystalSmithing(Items.NETHERITE_HELMET, Registration.CRYSTAL_HELMET, output);
        crystalSmithing(Items.NETHERITE_CHESTPLATE, Registration.CRYSTAL_CHESTPLATE, output);
        crystalSmithing(Items.NETHERITE_LEGGINGS, Registration.CRYSTAL_LEGGINGS, output);
        crystalSmithing(Items.NETHERITE_BOOTS, Registration.CRYSTAL_BOOTS, output);
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

    protected void crystalSmithing(Item ingredientItem, DeferredHolder<Item, ? extends Item> resultItem, RecipeOutput output) {
        ResourceKey<Recipe<?>> resourceKey = ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(resultItem.getId() + "_smithing"));

        Advancement.Builder advancement$builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceKey))
                .rewards(AdvancementRewards.Builder.recipe(resourceKey))
                .requirements(AdvancementRequirements.Strategy.OR)
                .addCriterion("has_crystal", HAS_CRYSTAL);

        CrystalSmithingRecipe smithingtransformrecipe = new CrystalSmithingRecipe(Optional.of(Ingredient.of(Registration.CRYSTAL_UPGRADE_SMITHING_TEMPLATE.get())),
                Ingredient.of(ingredientItem), Optional.of(Ingredient.of(Registration.CRYSTAL.get())),
                new TransmuteResult(resultItem.get()));

        output.accept(resourceKey, smithingtransformrecipe,
                advancement$builder.build(resourceKey.location().withPrefix("recipes/" + RecipeCategory.TOOLS.getFolderName() + "/")));
    }
}
