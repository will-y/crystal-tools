package dev.willyelton.crystal_tools.common.compat.jei;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.compat.jei.generator.GeneratorRecipe;
import dev.willyelton.crystal_tools.common.compat.jei.pedestal.PedestalRecipe;
import mezz.jei.api.recipe.types.IRecipeType;

public class CrystalToolsRecipeTypes {
    public static final IRecipeType<GeneratorRecipe> GENERATOR =
            IRecipeType.create(CrystalTools.MODID, "generator", GeneratorRecipe.class);

    public static final IRecipeType<PedestalRecipe> PEDESTAL =
            IRecipeType.create(CrystalTools.MODID, "pedestal", PedestalRecipe.class);
}
