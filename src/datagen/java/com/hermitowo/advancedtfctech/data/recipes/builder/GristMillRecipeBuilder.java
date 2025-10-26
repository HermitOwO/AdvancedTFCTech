package com.hermitowo.advancedtfctech.data.recipes.builder;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.data.recipes.builder.BaseHelpers;
import blusunrize.immersiveengineering.data.recipes.builder.IERecipeBuilder;
import com.hermitowo.advancedtfctech.common.recipes.GristMillRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class GristMillRecipeBuilder extends IERecipeBuilder<GristMillRecipeBuilder> implements BaseHelpers.ItemInput<GristMillRecipeBuilder>
{
    private IngredientWithSize input;
    private ItemStackProvider output;
    private int energy;
    private int time;

    private GristMillRecipeBuilder()
    {
    }

    public static GristMillRecipeBuilder builder()
    {
        return new GristMillRecipeBuilder();
    }

    @Override
    public GristMillRecipeBuilder input(IngredientWithSize input)
    {
        this.input = input;
        return this;
    }

    public GristMillRecipeBuilder output(ItemStackProvider output)
    {
        this.output = output;
        return this;
    }

    public GristMillRecipeBuilder setTime(int time)
    {
        this.time = time;
        return this;
    }

    public GristMillRecipeBuilder setEnergy(int energy)
    {
        this.energy = energy;
        return this;
    }

    public void build(RecipeOutput out, ResourceLocation name)
    {
        GristMillRecipe recipe = new GristMillRecipe(output, input, time, energy);
        out.accept(name, recipe, null, getConditions());
    }
}
