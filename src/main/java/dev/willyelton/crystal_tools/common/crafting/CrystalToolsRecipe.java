package dev.willyelton.crystal_tools.common.crafting;

import dev.willyelton.crystal_tools.common.capability.Capabilities;
import dev.willyelton.crystal_tools.common.capability.Levelable;
import dev.willyelton.crystal_tools.common.components.DataComponents;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import dev.willyelton.crystal_tools.common.levelable.skill.SkillPoints;
import dev.willyelton.crystal_tools.utils.ToolUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;

public abstract class CrystalToolsRecipe extends CustomRecipe {
    public CrystalToolsRecipe(CraftingBookCategory category) {
        super(category);
    }

    public abstract List<ItemStack> getInputs();

    public abstract ItemStack getOutput();

    protected int getPoints(ItemStack stack) {
        SkillPoints points = stack.getOrDefault(DataComponents.SKILL_POINT_DATA, new SkillPoints());
        return points.getTotalPoints() + stack.getOrDefault(DataComponents.SKILL_POINTS, 0);
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(getIngredients());
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapelessCraftingRecipeDisplay(getIngredients().stream().map(Ingredient::display).toList(),
                new SlotDisplay.ItemStackSlotDisplay(getOutput()), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    protected List<Ingredient> getIngredients() {
        return getInputs().stream().map(ItemStack::getItem).map(Ingredient::of).toList();
    }

    protected void increaseLevelCap(ItemStack stack, HolderLookup.Provider provider, int points) {
        if (provider instanceof RegistryAccess registryAccess) {
            Levelable levelable = stack.getCapability(Capabilities.ITEM_SKILL, registryAccess);

            if (levelable != null) {
                levelable.increaseExpCap(points);
                return;
            }
        }

        // Fallback if for some reason we don't have a real registry access?
        int newCap = ToolUtils.getNewCap(CrystalToolsConfig.BASE_EXPERIENCE_CAP.get(), points);

        stack.set(DataComponents.EXPERIENCE_CAP, newCap);
    }
}
