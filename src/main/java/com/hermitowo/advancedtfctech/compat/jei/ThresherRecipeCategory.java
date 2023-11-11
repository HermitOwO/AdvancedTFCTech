package com.hermitowo.advancedtfctech.compat.jei;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import com.hermitowo.advancedtfctech.common.recipes.ThresherRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.compat.jei.category.BaseRecipeCategory;

public class ThresherRecipeCategory extends BaseRecipeCategory<ThresherRecipe>
{
    private static final ResourceLocation ICONS = AdvancedTFCTech.rl("textures/gui/jei/jei.png");
    private final IDrawableStatic slot;
    private final IDrawableStatic gears;
    private final IDrawableAnimated gearsAnimated;

    public ThresherRecipeCategory(RecipeType<ThresherRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(120, 38), ATTMultiblockLogic.THRESHER.iconStack());
        gears = helper.createDrawable(ICONS, 0, 134, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 134, 22, 16);
        this.gearsAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ThresherRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 20, 11);
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 1);
        IRecipeSlotBuilder secondaryOutput = builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 21);

        input.addItemStacks(collapse(new ItemStackIngredient(recipe.input.getBaseIngredient(), recipe.input.getCount())));
        output.addItemStacks(collapse(recipe.input.getMatchingStackList(), recipe.output));
        recipe.secondaryOutputs.forEach(lazy -> secondaryOutput.addItemStack(lazy.get()));

        input.setBackground(slot, -1, -1);
        output.setBackground(slot, -1, -1);
        secondaryOutput.setBackground(slot, -1, -1);
    }

    @Override
    public void draw(ThresherRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics graphics, double mouseX, double mouseY)
    {
        gears.draw(graphics, 49, 11);
        gearsAnimated.draw(graphics, 49, 11);
    }
}