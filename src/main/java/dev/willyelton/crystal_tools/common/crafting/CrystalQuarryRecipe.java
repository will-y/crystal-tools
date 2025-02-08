package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.Registration;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.components.LevelableBlockEntityData;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Stream;

public class CrystalQuarryRecipe extends CrystalToolsRecipe {
    public CrystalQuarryRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) return false;

        List<ItemStack> inputs = getInputs();
        for (int i = 0; i < inputs.size(); i++) {
            if (!input.getItem(i % 3, i / 3).is(inputs.get(i).getItem())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(Registration.CRYSTAL_QUARRY_ITEM);

        ItemStack aiotItem = input.getItem(1, 1);

        int points = (int) (getPoints(aiotItem) * CrystalToolsConfig.QUARRY_INITIAL_POINT_MULTIPLIER.get());
        int cap = ToolUtils.getNewCap(CrystalToolsConfig.QUARRY_BASE_EXPERIENCE_CAP.get(), points);

        LevelableBlockEntityData skillData = new LevelableBlockEntityData(points, cap);
        result.set(DataComponents.LEVELABLE_BLOCK_ENTITY_DATA, skillData);

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registration.CRYSTAL_QUARRY_RECIPE.get();
    }

    @Override
    public List<ItemStack> getInputs() {
        return Stream.of(Registration.CRYSTAL_BLOCK_ITEM.get(), Registration.CRYSTAL.get(), Registration.CRYSTAL_BLOCK_ITEM.get(),
                Registration.CRYSTAL.get(), Registration.CRYSTAL_AIOT.get(), Registration.CRYSTAL.get(), Items.NETHERITE_INGOT,
                Items.NETHERITE_INGOT, Items.NETHERITE_INGOT).map(ItemStack::new).toList();
    }

    @Override
    public ItemStack getOutput() {
        ItemStack output = new ItemStack(Registration.CRYSTAL_QUARRY_ITEM.get());
        output.set(net.minecraft.core.component.DataComponents.LORE,
                new ItemLore(List.of(Component.literal(
                        String.format("Will take %d%% of the points from the AIOT used to craft this.", (int) (CrystalToolsConfig.QUARRY_INITIAL_POINT_MULTIPLIER.get() * 100))))));
        return output;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(Registration.CRYSTAL_QUARRY.get());
    }
}
