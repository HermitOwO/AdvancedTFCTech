package com.hermitowo.advancedtfctech.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hermitowo.advancedtfctech.api.crafting.cache.CachedRecipeList;
import com.hermitowo.advancedtfctech.common.crafting.ATTSerializers;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

public class PowerLoomRecipe extends ATTMultiblockRecipe
{
    public static final CachedRecipeList<PowerLoomRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.POWER_LOOM);

    public final IngredientWithSize[] inputs;
    public final IngredientWithSize secondaryInput;
    public final Lazy<ItemStack> output;
    public final NonNullList<Lazy<ItemStack>> secondaryOutputs = NonNullList.create();
    public final ResourceLocation inProgressTexture;

    public PowerLoomRecipe(ResourceLocation id, Lazy<ItemStack> output, IngredientWithSize[] inputs, IngredientWithSize secondaryInput, ResourceLocation inProgressTexture, int time, int energy)
    {
        super(ItemStack.EMPTY, ATTRecipeTypes.POWER_LOOM, id);
        this.output = output;
        this.inputs = inputs;
        this.secondaryInput = secondaryInput;
        this.inProgressTexture = inProgressTexture;

        timeAndEnergy(time, energy);
        modifyTimeAndEnergy(ATTConfig.SERVER.powerLoom_timeModifier::get, ATTConfig.SERVER.powerLoom_energyModifier::get);

        setInputListWithSizes(Lists.newArrayList(this.inputs));
        this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.output.get()));
    }

    public PowerLoomRecipe addToSecondaryOutput(Lazy<ItemStack> output)
    {
        Preconditions.checkNotNull(output);
        secondaryOutputs.add(output);
        return this;
    }

    public static PowerLoomRecipe findRecipe(Level level, ItemStack pirn, ItemStack weave)
    {
        if (pirn.isEmpty() || weave.isEmpty())
            return null;
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe.matches(pirn, weave))
                return recipe;
        return null;
    }

    public static PowerLoomRecipe findRecipeForRendering(Level level, ItemStack secondaryInput)
    {
        if (secondaryInput.isEmpty())
            return null;
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe.isValidSecondaryInput(secondaryInput))
                return recipe;
        return null;
    }

    public boolean matches(ItemStack pirn, ItemStack weave)
    {
        return isValidPirn(pirn) && isValidWeaveWithSize(weave);
    }

    public boolean isValidPirn(ItemStack stack)
    {
        return this.inputs[1] != null && this.inputs[1].test(stack);
    }

    public static boolean isValidPirnInput(Level level, ItemStack stack)
    {
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe != null && recipe.isValidPirn(stack))
                return true;
        return false;
    }

    public boolean isValidWeave(ItemStack stack)
    {
        return this.inputs[0] != null && this.inputs[0].testIgnoringSize(stack);
    }

    public boolean isValidWeaveWithSize(ItemStack stack)
    {
        return this.inputs[0] != null && this.inputs[0].test(stack);
    }

    public static boolean isValidWeaveInput(Level level, ItemStack stack)
    {
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe != null && recipe.isValidWeave(stack))
                return true;
        return false;
    }

    public boolean isValidSecondaryInput(ItemStack stack)
    {
        return this.secondaryInput != null && this.secondaryInput.testIgnoringSize(stack);
    }

    public static boolean isValidSecondaryInput(Level level, ItemStack stack)
    {
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe != null && recipe.isValidSecondaryInput(stack))
                return true;
        return false;
    }

    @Override
    public int getMultipleProcessTicks()
    {
        return 0;
    }

    @Override
    protected IERecipeSerializer<PowerLoomRecipe> getIESerializer()
    {
        return ATTSerializers.POWER_LOOM_SERIALIZER.get();
    }
}
