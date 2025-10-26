package com.hermitowo.advancedtfctech.data.recipes.builder;

import blusunrize.immersiveengineering.data.recipes.builder.BaseHelpers;
import blusunrize.immersiveengineering.data.recipes.builder.IERecipeBuilder;
import com.hermitowo.advancedtfctech.common.recipes.FleshingMachineRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class FleshingMachineRecipeBuilder extends IERecipeBuilder<FleshingMachineRecipeBuilder> implements BaseHelpers.UnsizedItemInput<FleshingMachineRecipeBuilder>
{
    private Ingredient input;
    private ItemStackProvider output;
    private int energy;
    private int time;

    private FleshingMachineRecipeBuilder()
    {
    }

    public static FleshingMachineRecipeBuilder builder()
    {
        return new FleshingMachineRecipeBuilder();
    }

    @Override
    public FleshingMachineRecipeBuilder input(Ingredient input)
    {
        this.input = input;
        return this;
    }

    public FleshingMachineRecipeBuilder output(ItemStackProvider output)
    {
        this.output = output;
        return this;
    }

    public FleshingMachineRecipeBuilder setTime(int time)
    {
        this.time = time;
        return this;
    }

    public FleshingMachineRecipeBuilder setEnergy(int energy)
    {
        this.energy = energy;
        return this;
    }

    public void build(RecipeOutput out, ResourceLocation name)
    {
        FleshingMachineRecipe recipe = new FleshingMachineRecipe(output, input, time, energy);
        out.accept(name, recipe, null, getConditions());
    }
}
