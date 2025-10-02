package dev.willyelton.crystal_tools.common.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.ModRegistration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.utils.EnchantmentUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;

import java.util.Optional;

public class CrystalSmithingRecipe extends SmithingTransformRecipe {

    public CrystalSmithingRecipe(Optional<Ingredient> template, Ingredient base, Optional<Ingredient> addition, TransmuteResult result) {
        super(template, base, addition, result);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registryAccess) {
        ItemStack result = super.assemble(input, registryAccess);

        int finalPoints = EnchantmentUtils.pointsFromEnchantments(result);
        // If the previous tool is levelable
        SkillPoints points = result.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints());
        finalPoints += points.getTotalPoints() + result.getOrDefault(DataComponents.SKILL_POINTS, 0);

        // Clear any copied datacomponents
        result.applyComponents(result.getItem().components());
        result.set(DataComponents.SKILL_POINTS, finalPoints);

        return result;
    }

    @Override
    public RecipeSerializer<SmithingTransformRecipe> getSerializer() {
        return ModRegistration.CRYSTAL_SMITHING_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<SmithingTransformRecipe> {
        private static final MapCodec<SmithingTransformRecipe> CODEC = RecordCodecBuilder.mapCodec(
                p_399419_ -> p_399419_.group(
                                Ingredient.CODEC.optionalFieldOf("template").forGetter(SmithingTransformRecipe::templateIngredient),
                                Ingredient.CODEC.fieldOf("base").forGetter(SmithingTransformRecipe::baseIngredient),
                                Ingredient.CODEC.optionalFieldOf("addition").forGetter(SmithingTransformRecipe::additionIngredient),
                                TransmuteResult.CODEC.fieldOf("result").forGetter(p_393285_ -> p_393285_.result)
                        )
                        .apply(p_399419_, CrystalSmithingRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
                SmithingTransformRecipe::templateIngredient,
                Ingredient.CONTENTS_STREAM_CODEC,
                SmithingTransformRecipe::baseIngredient,
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
                SmithingTransformRecipe::additionIngredient,
                TransmuteResult.STREAM_CODEC,
                p_393287_ -> p_393287_.result,
                CrystalSmithingRecipe::new
        );

        @Override
        public MapCodec<SmithingTransformRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmithingTransformRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
