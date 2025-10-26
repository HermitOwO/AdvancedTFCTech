package com.hermitowo.advancedtfctech.common.recipes.outputs;

import com.hermitowo.advancedtfctech.common.component.ATTComponents;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifierType;

public enum DoubleIfMachineMadeModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        if (input.getOrDefault(ATTComponents.MACHINE_MADE, false))
            stack.grow(stack.getCount());
        return stack;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ATTItemStackModifiers.DOUBLE_IF_MACHINE_MADE.get();
    }

}
