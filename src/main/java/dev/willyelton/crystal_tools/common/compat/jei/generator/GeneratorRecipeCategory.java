package dev.willyelton.crystal_tools.common.compat.jei.generator;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.compat.jei.CrystalToolsRecipeTypes;
import dev.willyelton.crystal_tools.common.config.CrystalToolsConfig;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class GeneratorRecipeCategory implements IRecipeCategory<GeneratorRecipe> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/gui/crystal_generator_category.png");
    private final IDrawableStatic background;
    private final LoadingCache<Integer, IDrawableAnimated> cachedFlames;

    public GeneratorRecipeCategory(IGuiHelper guiHelper) {

        this.background = guiHelper.drawableBuilder(TEXTURE, 0, 0, 18, 34)
                .addPadding(0, 0, 0, 20 + Minecraft.getInstance().font.width("Requires Upgrade: Metal Generator"))
                .build();

        this.cachedFlames = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer burnTime) {
                        return guiHelper.drawableBuilder(TEXTURE, 0, 37, 13, 13)
                                .buildAnimated(burnTime, IDrawableAnimated.StartDirection.TOP, true);
                    }
                });
    }

    @Override
    public RecipeType<GeneratorRecipe> getRecipeType() {
        return CrystalToolsRecipeTypes.GENERATOR;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.crystal_tools.category.generator");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GeneratorRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .setSlotName("Fuel").addItemStack(recipe.stack());
    }

    @Override
    public void draw(GeneratorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int burnTime = recipe.burnTime();
        IDrawableAnimated flame = cachedFlames.getUnchecked(burnTime);
        flame.draw(guiGraphics, 3, 19);

        int feGeneration = recipe.bonusGeneration() + CrystalToolsConfig.BASE_FE_GENERATION.get();

        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        Component burnTimeComponent = Component.literal(String.format("Burns for %d ticks", burnTime));
        Component feComponent = Component.literal(String.format("Generates %d FE per tick", feGeneration));
        guiGraphics.drawString(font, burnTimeComponent, 25, 0, 0xFF808080, false);
        guiGraphics.drawString(font, feComponent, 25, 10, 0xFF808080, false);

        if (!recipe.upgradeRequired().isEmpty()) {
            Component upgradeComponent = Component.literal("Requires Upgrade: " + recipe.upgradeRequired());
            guiGraphics.drawString(font, upgradeComponent, 25, 20, 0xFF40BABE, false);
        }
    }
}
