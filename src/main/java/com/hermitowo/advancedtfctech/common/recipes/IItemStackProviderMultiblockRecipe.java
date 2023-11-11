package com.hermitowo.advancedtfctech.common.recipes;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface IItemStackProviderMultiblockRecipe
{
    NonNullList<ItemStack> generateActualOutput(ItemStack input);
}
