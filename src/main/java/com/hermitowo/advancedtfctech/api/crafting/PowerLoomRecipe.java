package com.hermitowo.advancedtfctech.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.common.collect.Lists;
import com.hermitowo.advancedtfctech.api.crafting.cache.CachedRecipeList;
import com.hermitowo.advancedtfctech.common.crafting.ATTSerializers;
import com.hermitowo.advancedtfctech.config.ATTServerConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

public class PowerLoomRecipe extends ATTMultiblockRecipe
{
    public static final CachedRecipeList<PowerLoomRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.POWER_LOOM);

    public final IngredientWithSize[] inputs;
    public final Lazy<ItemStack> output;

    public PowerLoomRecipe(ResourceLocation id, Lazy<ItemStack> output, IngredientWithSize[] inputs, int time, int energy)
    {
        super(ItemStack.EMPTY, ATTRecipeTypes.POWER_LOOM, id);
        this.output = output;
        this.inputs = inputs;

        timeAndEnergy(time, energy);
        modifyTimeAndEnergy(ATTServerConfig.GENERAL.powerLoom_timeModifier::get, ATTServerConfig.GENERAL.powerLoom_energyModifier::get);

        setInputListWithSizes(Lists.newArrayList(this.inputs));
        this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.output.get()));
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

    public boolean matches(ItemStack pirn, ItemStack weave)
    {
        return isValidPirn(pirn) && isValidWeave(weave);
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

    public static boolean isValidWeaveInput(Level level, ItemStack stack)
    {
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe != null && recipe.isValidWeave(stack))
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
