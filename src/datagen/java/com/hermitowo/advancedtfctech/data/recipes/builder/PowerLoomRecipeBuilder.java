package com.hermitowo.advancedtfctech.data.recipes.builder;

import java.util.ArrayList;
import java.util.List;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.data.recipes.builder.BaseHelpers;
import blusunrize.immersiveengineering.data.recipes.builder.IERecipeBuilder;
import com.hermitowo.advancedtfctech.common.recipes.PowerLoomRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class PowerLoomRecipeBuilder extends IERecipeBuilder<PowerLoomRecipeBuilder> implements BaseHelpers.ItemInput<PowerLoomRecipeBuilder>, BaseHelpers.ItemOutput<PowerLoomRecipeBuilder>
{
    private final List<IngredientWithSize> inputs = new ArrayList<>();
    private IngredientWithSize secondaryInput;
    private TagOutput output;
    private ItemStack secondaryOutput;
    private ResourceLocation inProgressTexture;
    private int energy;
    private int time;

    private PowerLoomRecipeBuilder()
    {
    }

    public static PowerLoomRecipeBuilder builder()
    {
        return new PowerLoomRecipeBuilder();
    }

    @Override
    public PowerLoomRecipeBuilder input(IngredientWithSize input)
    {
        this.inputs.add(input);
        return this;
    }

    public PowerLoomRecipeBuilder secondaryInput(ItemLike secondaryInput, int count)
    {
        return secondaryInput(new IngredientWithSize(Ingredient.of(secondaryInput), count));
    }

    public PowerLoomRecipeBuilder secondaryInput(IngredientWithSize secondaryInput)
    {
        this.secondaryInput = secondaryInput;
        return this;
    }

    @Override
    public PowerLoomRecipeBuilder output(TagOutput output)
    {
        this.output = output;
        return this;
    }

    public PowerLoomRecipeBuilder secondaryOutput(ItemLike secondaryOutput)
    {
        return secondaryOutput(new ItemStack(secondaryOutput));
    }

    public PowerLoomRecipeBuilder secondaryOutput(ItemStack secondaryOutput)
    {
        this.secondaryOutput = secondaryOutput;
        return this;
    }

    public PowerLoomRecipeBuilder inProgressTexture(String inProgressTexture)
    {
        return inProgressTexture(ResourceLocation.parse(inProgressTexture));
    }

    public PowerLoomRecipeBuilder inProgressTexture(ResourceLocation inProgressTexture)
    {
        this.inProgressTexture = inProgressTexture;
        return this;
    }

    public PowerLoomRecipeBuilder setTime(int time)
    {
        this.time = time;
        return this;
    }

    public PowerLoomRecipeBuilder setEnergy(int energy)
    {
        this.energy = energy;
        return this;
    }

    public void build(RecipeOutput out, ResourceLocation name)
    {
        PowerLoomRecipe recipe = new PowerLoomRecipe(output, secondaryOutput, inputs, secondaryInput, inProgressTexture, time, energy);
        out.accept(name, recipe, null, getConditions());
    }
}
