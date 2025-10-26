package com.hermitowo.advancedtfctech.common.recipes;

import java.util.List;
import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public abstract class ATTMultiblockRecipe extends MultiblockRecipe
{
    protected <T extends Recipe<?>> ATTMultiblockRecipe(TagOutput outputDummy, IERecipeTypes.TypeWithClass<T> type, int baseTime, int baseEnergy, Supplier<RecipeMultiplier> multipliers)
    {
        super(outputDummy, type, baseTime, baseEnergy, multipliers);
    }

    public ItemStack getSecondaryOutput()
    {
        return ItemStack.EMPTY;
    }

    protected List<ItemStackProvider> providerList;
}
