package com.hermitowo.advancedtfctech.compat.jei;

import java.util.Arrays;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import com.hermitowo.advancedtfctech.common.recipes.outputs.DoubleIfHasTagModifier;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.compat.jei.category.BaseRecipeCategory;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class BeamhouseRecipeCategory extends BaseRecipeCategory<BeamhouseRecipe>
{
    private static final ResourceLocation ICONS = new ResourceLocation(MOD_ID, "textures/gui/jei/jei.png");
    private final IDrawableStatic slot;
    private final IDrawableStatic arrows;
    private final IDrawableAnimated arrowsAnimated;

    public BeamhouseRecipeCategory(RecipeType<BeamhouseRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(98, 26), new ItemStack(ATTBlocks.Multiblocks.BEAMHOUSE.get()));
        arrows = helper.createDrawable(ICONS, 0, 118, 22, 16);
        IDrawableStatic arrowAnimated = helper.createDrawable(ICONS, 22, 118, 22, 16);
        this.arrowsAnimated = helper.createAnimatedDrawable(arrowAnimated, 80, IDrawableAnimated.StartDirection.LEFT, false);
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BeamhouseRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder fluidInput = builder.addSlot(RecipeIngredientRole.INPUT, 6, 5);
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 26, 5);
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5);

        fluidInput.addIngredients(ForgeTypes.FLUID_STACK, recipe.fluidInput.getMatchingFluidStacks());
        input.addItemStacks(recipe.input.getMatchingStackList());
        output.addItemStacks(collapse(recipe.input.getMatchingStackList(), recipe.output));

        fluidInput.setBackground(slot, -1, -1);
        input.setBackground(slot, -1, -1);
        output.setBackground(slot, -1, -1);

        fluidInput.setFluidRenderer(1, false, 16, 16);

        if (Arrays.stream(recipe.output.modifiers()).anyMatch(modifier -> modifier.getClass().equals(DoubleIfHasTagModifier.class)))
            output.addTooltipCallback((slots, tooltip) -> tooltip.add(new TranslatableComponent("advancedtfctech.jei.double_if_has_tag").withStyle(ChatFormatting.ITALIC)));
    }

    @Override
    public void draw(BeamhouseRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        arrows.draw(stack, 48, 5);
        arrowsAnimated.draw(stack, 48, 5);
    }
}
