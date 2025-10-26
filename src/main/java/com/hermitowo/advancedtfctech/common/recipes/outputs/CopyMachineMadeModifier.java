package com.hermitowo.advancedtfctech.common.recipes.outputs;

import com.hermitowo.advancedtfctech.common.component.ATTComponents;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifierType;

public enum CopyMachineMadeModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        if (input.getOrDefault(ATTComponents.MACHINE_MADE, false))
            stack.set(ATTComponents.MACHINE_MADE, true);
        return stack;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ATTItemStackModifiers.COPY_MACHINE_MADE.get();
    }
}
