package com.hermitowo.advancedtfctech.compat.jei;

import java.util.Arrays;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.recipes.FleshingMachineRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.compat.jei.category.BaseRecipeCategory;

public class FleshingMachineRecipeCategory extends BaseRecipeCategory<FleshingMachineRecipe>
{
    private final IDrawableStatic slot;

    public FleshingMachineRecipeCategory(RecipeType<FleshingMachineRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(120, 38), new ItemStack(ATTBlocks.FLESHING_MACHINE.get()));
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FleshingMachineRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 8, 11);
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 11);
        IRecipeSlotBuilder blades = builder.addSlot(RecipeIngredientRole.CATALYST, 69, 1);

        input.addIngredients(recipe.input);
        output.addItemStacks(collapse(Arrays.asList(recipe.input.getItems()), recipe.output));
        blades.addItemStack(new ItemStack(ATTItems.FLESHING_BLADES.get().asItem()));

        input.setBackground(slot, -1, -1);
        output.setBackground(slot, -1, -1);
        blades.setBackground(slot, -1, -1);
    }

    @Override
    public void draw(FleshingMachineRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
    {
        PoseStack stack = graphics.pose();
        stack.pushPose();
        stack.scale(1.81F, 1.81F, 1);
        this.getIcon().draw(graphics, 18, 1);
        stack.popPose();
    }
}
