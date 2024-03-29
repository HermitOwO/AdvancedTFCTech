package com.hermitowo.advancedtfctech.compat.jei;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import com.hermitowo.advancedtfctech.common.recipes.GristMillRecipe;
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

public class GristMillRecipeCategory extends BaseRecipeCategory<GristMillRecipe>
{
    private static final ResourceLocation ICONS = AdvancedTFCTech.rl("textures/gui/jei/jei.png");
    private final IDrawableStatic slot;
    private final IDrawableStatic gears;
    private final IDrawableAnimated gearsAnimated;

    public GristMillRecipeCategory(RecipeType<GristMillRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(120, 38), ATTMultiblockLogic.GRIST_MILL.iconStack());
        gears = helper.createDrawable(ICONS, 0, 134, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 134, 22, 16);
        this.gearsAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GristMillRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 20, 11);
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 11);

        input.addItemStacks(collapse(new ItemStackIngredient(recipe.input.getBaseIngredient(), recipe.input.getCount())));
        output.addItemStacks(collapse(recipe.input.getMatchingStackList(), recipe.output));

        input.setBackground(slot, -1, -1);
        output.setBackground(slot, -1, -1);
    }

    @Override
    public void draw(GristMillRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics graphics, double mouseX, double mouseY)
    {
        gears.draw(graphics, 49, 11);
        gearsAnimated.draw(graphics, 49, 11);
    }
}