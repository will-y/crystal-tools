package dev.willyelton.crystal.core.datagen;

import dev.willyelton.crystal.core.Registration;
import dev.willyelton.crystal.core.utils.constants.ApiConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CrystalCoreRecipes extends RecipeProvider {
    public static final Criterion<InventoryChangeTrigger.TriggerInstance> HAS_CRYSTAL = InventoryChangeTrigger.TriggerInstance.hasItems(Registration.CRYSTAL.get());

    public CrystalCoreRecipes(BootstrapContext<Recipe<?>> recipeOutput, BootstrapContext<Advancement> advancementOutput) {
        super(recipeOutput, advancementOutput);
    }

    @Override
    protected void buildRecipes() {
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
    }
}
