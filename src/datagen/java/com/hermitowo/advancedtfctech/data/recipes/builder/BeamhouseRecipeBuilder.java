package com.hermitowo.advancedtfctech.data.recipes.builder;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.data.recipes.builder.BaseHelpers;
import blusunrize.immersiveengineering.data.recipes.builder.IERecipeBuilder;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import com.hermitowo.advancedtfctech.common.recipes.GristMillRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class BeamhouseRecipeBuilder extends IERecipeBuilder<BeamhouseRecipeBuilder> implements BaseHelpers.ItemInput<BeamhouseRecipeBuilder>
{
    private IngredientWithSize input;
    private SizedFluidIngredient fluidInput;
    private ItemStackProvider output;
    private int energy;
    private int time;

    private BeamhouseRecipeBuilder()
    {
    }

    public static BeamhouseRecipeBuilder builder()
    {
        return new BeamhouseRecipeBuilder();
    }

    @Override
    public BeamhouseRecipeBuilder input(IngredientWithSize input)
    {
        this.input = input;
        return this;
    }

    public BeamhouseRecipeBuilder output(ItemStackProvider output)
    {
        this.output = output;
        return this;
    }

    public BeamhouseRecipeBuilder fluidInput(SizedFluidIngredient fluidInput)
    {
        this.fluidInput = fluidInput;
        return this;
    }

    public BeamhouseRecipeBuilder setTime(int time)
    {
        this.time = time;
        return this;
    }

    public BeamhouseRecipeBuilder setEnergy(int energy)
    {
        this.energy = energy;
        return this;
    }

    public void build(RecipeOutput out, ResourceLocation name)
    {
        BeamhouseRecipe recipe = new BeamhouseRecipe(output, input, fluidInput, time, energy);
        out.accept(name, recipe, null, getConditions());
    }
}
