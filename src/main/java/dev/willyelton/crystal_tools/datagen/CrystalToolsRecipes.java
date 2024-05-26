package dev.willyelton.crystal_tools.datagen;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.crafting.ItemDisabledCondition;
import dev.willyelton.crystal_tools.crafting.ModRecipes;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CrystalToolsRecipes extends RecipeProvider {
    private static final CriterionTriggerInstance HAS_CRYSTAL = InventoryChangeTrigger.TriggerInstance.hasItems(Registration.CRYSTAL.get());
    public CrystalToolsRecipes(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        // Basic
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Registration.CRYSTAL.get(), 9)
                .requires(Registration.CRYSTAL_BLOCK.get())
                .unlockedBy("has_crystal_block",
                        InventoryChangeTrigger.TriggerInstance.hasItems(Registration.CRYSTAL_BLOCK.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Registration.CRYSTAL_BLOCK.get())
                .pattern("ccc")
                .pattern("ccc")
                .pattern("ccc")
                .define('c', Registration.CRYSTAL.get())
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration. NETHERITE_STICK.get(), 4)
                .pattern("n")
                .pattern("n")
                .define('n', Tags.Items.INGOTS_NETHERITE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration. CRYSTAL_TORCH.get(), 4)
                .pattern("c")
                .pattern("s")
                .define('c', Registration.CRYSTAL.get())
                .define('s', Items.STICK)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(consumer);

        // Tools
        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_AXE.get())
                    .pattern("cc")
                    .pattern("cs")
                    .pattern(" s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', Registration.NETHERITE_STICK.get())
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_AXE.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_HOE.get())
                    .pattern("cc")
                    .pattern(" s")
                    .pattern(" s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', Registration.NETHERITE_STICK.get())
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_HOE.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_PICKAXE.get())
                    .pattern("ccc")
                    .pattern(" s ")
                    .pattern(" s ")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', Registration.NETHERITE_STICK.get())
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_PICKAXE.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_SHOVEL.get())
                    .pattern("c")
                    .pattern("s")
                    .pattern("s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', Registration.NETHERITE_STICK.get())
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_SHOVEL.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Registration.CRYSTAL_SWORD.get())
                    .pattern("c")
                    .pattern("c")
                    .pattern("s")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', Registration.NETHERITE_STICK.get())
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_SWORD.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Registration.CRYSTAL_BOW.get())
                    .pattern(" sc")
                    .pattern("s c")
                    .pattern(" sc")
                    .define('c', Registration.CRYSTAL.get())
                    .define('s', Registration.NETHERITE_STICK.get())
                    .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_BOW.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, Registration.CRYSTAL_APPLE.get())
                        .pattern("ccc")
                        .pattern("cac")
                        .pattern("ccc")
                        .define('c', Registration.CRYSTAL.get())
                        .define('a', Items.ENCHANTED_GOLDEN_APPLE)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_APPLE.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_ROCKET.get())
                        .pattern("ccc")
                        .pattern("crc")
                        .pattern("ccc")
                        .define('c', Registration.CRYSTAL.get())
                        .define('r', Items.FIREWORK_ROCKET)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_ROCKET.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_TRIDENT.get())
                        .pattern("ccc")
                        .pattern("sts")
                        .pattern(" s ")
                        .define('c', Registration.CRYSTAL.get())
                        .define('t', Items.TRIDENT)
                        .define('s', Registration.NETHERITE_STICK.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_TRIDENT.getId());

        // Armor
        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_HELMET.get())
                        .pattern("ccc")
                        .pattern("c c")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_HELMET.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_CHESTPLATE.get())
                        .pattern("c c")
                        .pattern("ccc")
                        .pattern("ccc")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_CHESTPLATE.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_LEGGINGS.get())
                        .pattern("ccc")
                        .pattern("c c")
                        .pattern("c c")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_LEGGINGS.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_BOOTS.get())
                        .pattern("c c")
                        .pattern("c c")
                        .define('c', Registration.CRYSTAL.get())
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_BOOTS.getId());

        buildConditionalRecipe(consumer,
                ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Registration.CRYSTAL_BACKPACK.get())
                        .pattern("lcl")
                        .pattern("chc")
                        .pattern("lcl")
                        .define('l', Items.LEATHER)
                        .define('c', Registration.CRYSTAL.get())
                        .define('h', Items.CHEST)
                        .unlockedBy("has_crystal", HAS_CRYSTAL),
                Registration.CRYSTAL_BACKPACK.getId());

        // Machines
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Registration.CRYSTAL_FURNACE.get())
                .pattern("ccc")
                .pattern("cfc")
                .pattern("ccc")
                .define('c', Registration.CRYSTAL.get())
                .define('f', Items.FURNACE)
                .unlockedBy("has_crystal", HAS_CRYSTAL)
                .save(consumer);

        // Special
        SpecialRecipeBuilder
                .special(ModRecipes.CRYSTAL_AIOT_RECIPE.get())
                .save(consumer, Registration.CRYSTAL_AIOT.getId().toString());

        SpecialRecipeBuilder
                .special(ModRecipes.CRYSTAL_ELYTRA_RECIPE.get())
                .save(consumer, Registration.CRYSTAL_ELYTRA.getId().toString());
    }

    private void buildConditionalRecipe(Consumer<FinishedRecipe> consumer, ShapedRecipeBuilder builder, ResourceLocation conditionItem) {
        ConditionalRecipe.builder()
                .addCondition(new NotCondition(new ItemDisabledCondition(conditionItem)))
                .addRecipe(builder::save)
                .build(consumer, conditionItem);
    }
}
