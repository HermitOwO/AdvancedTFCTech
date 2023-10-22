package com.hermitowo.advancedtfctech.common;

import com.hermitowo.advancedtfctech.common.items.ATTItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ATTTabs
{
    public static final CreativeModeTab MAIN = new CreativeModeTab("advancedtfctech")
    {
        @NonNull
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(ATTItems.PIRN.get());
        }
    };
}
