package com.hermitowo.advancedtfctech.common.recipes;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public abstract class ATTMultiblockRecipe extends MultiblockRecipe
{
    Lazy<Integer> totalProcessTime;
    Lazy<Integer> totalProcessEnergy;

    protected ATTMultiblockRecipe(ItemStack outputDummy, Supplier<? extends RecipeType<?>> type, ResourceLocation id)
    {
        super(Lazy.of(() -> outputDummy), type.get(), id);
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

    protected Lazy<NonNullList<ItemStackProvider>> providerList = Lazy.of(NonNullList::create);
}
