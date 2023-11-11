package com.hermitowo.advancedtfctech.common.recipes;

import java.util.function.DoubleSupplier;
import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public abstract class ATTMultiblockRecipe extends MultiblockRecipe
{
    Lazy<Integer> totalProcessTime;
    Lazy<Integer> totalProcessEnergy;

    protected <T extends Recipe<?>> ATTMultiblockRecipe(Lazy<ItemStack> outputDummy, IERecipeTypes.TypeWithClass<T> type, ResourceLocation id)
    {
        super(outputDummy, type, id);
    }

    protected void timeAndEnergy(int time, int energy)
    {
        this.totalProcessTime = Lazy.of(() -> time);
        this.totalProcessEnergy = Lazy.of(() -> energy);
    }

    @Override
    public void modifyTimeAndEnergy(DoubleSupplier timeModifier, DoubleSupplier energyModifier)
    {
        final Lazy<Integer> oldTime = this.totalProcessTime;
        final Lazy<Integer> oldEnergy = this.totalProcessEnergy;
        this.totalProcessTime = Lazy.of(() -> (int) (Math.max(1, oldTime.get() * timeModifier.getAsDouble())));
        this.totalProcessEnergy = Lazy.of(() -> (int) (Math.max(1, oldEnergy.get() * energyModifier.getAsDouble())));
    }

    @Override
    public int getTotalProcessTime()
    {
        return this.totalProcessTime.get();
    }

    @Override
    public int getTotalProcessEnergy()
    {
        return this.totalProcessEnergy.get();
    }

    public NonNullList<Lazy<ItemStack>> getSecondaryOutputs()
    {
        return NonNullList.create();
    }

    protected Lazy<NonNullList<ItemStackProvider>> providerList = Lazy.of(NonNullList::create);
}
