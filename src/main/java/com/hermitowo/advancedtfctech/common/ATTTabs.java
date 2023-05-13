package com.hermitowo.advancedtfctech.common;

import com.hermitowo.advancedtfctech.common.items.ATTItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ATTTabs
{
    public static final CreativeModeTab MAIN = new CreativeModeTab("advancedtfctech")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(ATTItems.PIRN.get());
        }
    };
}
