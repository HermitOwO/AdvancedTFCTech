package com.hermitowo.advancedtfctech.client;

import java.util.List;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;

import net.dries007.tfc.util.Helpers;

public class ATTClientForgeEvents
{
    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(ATTClientForgeEvents::onTooltip);
    }

    private static void onTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final List<Component> text = event.getToolTip();
        if (!stack.isEmpty())
            if (!ModList.get().isLoaded("firmalife"))
                if (Helpers.isItem(stack, ATTItems.PINEAPPLE_WINDED_PIRN.get()))
                    text.add(Helpers.translatable("advancedtfctech.tooltip.firmalife_not_loaded"));
    }
}
