package com.hermitowo.advancedtfctech.compat.jei;

import java.util.List;
import com.hermitowo.advancedtfctech.api.crafting.ThresherRecipe;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.compat.jei.category.BaseRecipeCategory;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ThresherRecipeCategory extends BaseRecipeCategory<ThresherRecipe>
{
    private static final ResourceLocation ICONS = new ResourceLocation(MOD_ID, "textures/gui/jei/jei.png");
    private final IDrawableStatic slot;
    private final IDrawableStatic gears;
    private final IDrawableAnimated gearsAnimated;

    public ThresherRecipeCategory(RecipeType<ThresherRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(120, 38), new ItemStack(ATTBlocks.Multiblocks.THRESHER.get()));
        gears = helper.createDrawable(ICONS, 0, 134, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 134, 22, 16);
        this.gearsAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ThresherRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 20, 10);
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 0);
        IRecipeSlotBuilder secondaryOutput = builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 20);

        final Ingredient ingredient = recipe.input.getBaseIngredient();
        final List<ItemStack> inputList = List.of(ingredient.getItems());

        input.addItemStacks(collapse(new ItemStackIngredient(recipe.input.getBaseIngredient(), recipe.input.getCount())));
        output.addItemStacks(collapse(inputList, recipe.output));
        for (Lazy<ItemStack> out : recipe.secondaryOutputs)
        {
            secondaryOutput.addItemStack(out.get());
        }

        input.setBackground(slot, -1, -1);
        output.setBackground(slot, -1, -1);
        secondaryOutput.setBackground(slot, -1, -1);
    }

    @Override
    public void draw(ThresherRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        gears.draw(stack, 50, 12);
        gearsAnimated.draw(stack, 50, 12);
    }
}