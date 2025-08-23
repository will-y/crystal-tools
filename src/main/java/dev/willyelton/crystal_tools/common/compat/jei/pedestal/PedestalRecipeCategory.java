package dev.willyelton.crystal_tools.common.compat.jei.pedestal;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.common.compat.jei.CrystalToolsRecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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

public class PedestalRecipeCategory implements IRecipeCategory<PedestalRecipe>  {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CrystalTools.MODID, "textures/gui/crystal_generator_category.png");

    private final IDrawableStatic background;

    public PedestalRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(TEXTURE, 0, 0, 18, 18)
//                .addPadding(0, 0, 0, 20 + 100)
                .build();
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 100;
    }

    @Override
    public RecipeType<PedestalRecipe> getRecipeType() {
        return CrystalToolsRecipeTypes.PEDESTAL;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.crystal_tools.category.pedestal");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PedestalRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .setSlotName("Pedestal Item")
                .addItemStack(recipe.stack());
    }

    @Override
    public void draw(PedestalRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        Component titleComponent = Component.translatable(recipe.data().type().name());
        Component descriptionComponent = Component.translatable(recipe.data().type().getDescription());

        guiGraphics.drawString(font, titleComponent, 25, 0, 0xFF000000, false);
        guiGraphics.drawWordWrap(font, descriptionComponent, 25, 10, 100, 0xFF606060);
        int y = font.split(descriptionComponent, 100).size() * 9;

        if (recipe.data().params() != null && recipe.data().params().range() > 0) {
            guiGraphics.drawString(font, Component.literal("Range: +" + recipe.data().params().range()), 25, 11 + y, 0xFF8080FF, false);
        }
    }
}
