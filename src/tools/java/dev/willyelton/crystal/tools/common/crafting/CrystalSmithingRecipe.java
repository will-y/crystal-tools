package dev.willyelton.crystal.tools.common.crafting;

import dev.willyelton.crystal.core.common.datacomponent.DataComponents;
import dev.willyelton.crystal.core.common.skill.SkillPoints;
import dev.willyelton.crystal.core.utils.EnchantmentUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import java.util.Optional;

public class CrystalSmithingRecipe extends SmithingTransformRecipe {

    public CrystalSmithingRecipe(Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, ItemStackTemplate result) {
        super(new CommonInfo(true), template, base, addition, result);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input) {
        ItemStack result = super.assemble(input);

        int finalPoints = EnchantmentUtils.pointsFromEnchantments(result);
        // If the previous tool is levelable
        SkillPoints points = result.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints());
        finalPoints += points.getTotalPoints() + result.getOrDefault(DataComponents.SKILL_POINTS, 0);

        // Clear any copied datacomponents
        result.applyComponents(result.getItem().components());
        result.set(DataComponents.SKILL_POINTS, finalPoints);

        return result;
    }
}
