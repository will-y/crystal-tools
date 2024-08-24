package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.LevelableBlockEntityData;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Stream;

public class CrystalGeneratorRecipe extends CrystalToolsRecipe {
    public CrystalGeneratorRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) return false;

        return input.getItem(0, 0).is(Items.REDSTONE_BLOCK) &&
                input.getItem(0, 1).is(Registration.CRYSTAL.get()) &&
                input.getItem(0, 2).is(Items.REDSTONE_BLOCK) &&
                input.getItem(1, 0).is(Registration.CRYSTAL.get()) &&
                input.getItem(1, 1).is(Registration.CRYSTAL_FURNACE_ITEM.get()) &&
                input.getItem(1, 2).is(Registration.CRYSTAL.get()) &&
                input.getItem(2, 0).is(Items.REDSTONE_BLOCK) &&
                input.getItem(2, 1).is(Registration.CRYSTAL.get()) &&
                input.getItem(2, 2).is(Items.REDSTONE_BLOCK);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(Registration.CRYSTAL_GENERATOR_ITEM.get());

        ItemStack furnaceItem = input.getItem(1, 1);

        LevelableBlockEntityData furnaceData = furnaceItem.get(DataComponents.LEVELABLE_BLOCK_ENTITY_DATA);
        if (furnaceData != null) {
            int spentPoints = furnaceData.points().stream().mapToInt(Integer::intValue).sum();
            LevelableBlockEntityData generatorData = new LevelableBlockEntityData(furnaceData.skillPoints() + spentPoints);
            result.set(DataComponents.LEVELABLE_BLOCK_ENTITY_DATA, generatorData);
        }

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.CRYSTAL_GENERATOR_RECIPE.get();
    }

    @Override
    public List<ItemStack> getInputs() {
        return Stream.of(Items.REDSTONE_BLOCK, Registration.CRYSTAL.get(), Items.REDSTONE_BLOCK,
                Registration.CRYSTAL.get(),Registration.CRYSTAL_FURNACE_ITEM.get(), Registration.CRYSTAL.get(),
                Items.REDSTONE_BLOCK, Registration.CRYSTAL.get(), Items.REDSTONE_BLOCK).map(ItemStack::new).toList();
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Registration.CRYSTAL_GENERATOR_ITEM.get());
    }
}