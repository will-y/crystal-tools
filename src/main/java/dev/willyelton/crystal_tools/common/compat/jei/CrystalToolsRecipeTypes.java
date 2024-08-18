package dev.willyelton.crystal_tools.common.compat.jei;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.compat.jei.generator.GeneratorRecipe;
import mezz.jei.api.recipe.RecipeType;

public class CrystalToolsRecipeTypes {
    public static final RecipeType<GeneratorRecipe> GENERATOR =
            RecipeType.create(CrystalTools.MODID, "generator", GeneratorRecipe.class);
}
