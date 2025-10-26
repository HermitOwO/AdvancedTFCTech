package com.hermitowo.advancedtfctech.data.recipes.builder;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.data.recipes.builder.BaseHelpers;
import blusunrize.immersiveengineering.data.recipes.builder.IERecipeBuilder;
import com.hermitowo.advancedtfctech.common.recipes.ThresherRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class ThresherRecipeBuilder extends IERecipeBuilder<ThresherRecipeBuilder> implements BaseHelpers.ItemInput<ThresherRecipeBuilder>
{
    private IngredientWithSize input;
    private ItemStackProvider output;
    private ItemStack secondaryOutput;
    private int energy;
    private int time;

    private ThresherRecipeBuilder()
    {
    }

    public static ThresherRecipeBuilder builder()
    {
        return new ThresherRecipeBuilder();
    }

    @Override
    public ThresherRecipeBuilder input(IngredientWithSize input)
    {
        this.input = input;
        return this;
    }

    public ThresherRecipeBuilder output(ItemStackProvider output)
    {
        this.output = output;
        return this;
    }

    public ThresherRecipeBuilder secondaryOutput(ItemStack secondaryOutput)
    {
        this.secondaryOutput = secondaryOutput;
        return this;
    }

    public ThresherRecipeBuilder setTime(int time)
    {
        this.time = time;
        return this;
    }

    public ThresherRecipeBuilder setEnergy(int energy)
    {
        this.energy = energy;
        return this;
    }

    public void build(RecipeOutput out, ResourceLocation name)
    {
        ThresherRecipe recipe = new ThresherRecipe(output, input, secondaryOutput, time, energy);
        out.accept(name, recipe, null, getConditions());
    }
}
