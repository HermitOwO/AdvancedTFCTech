package com.hermitowo.advancedtfctech.client;

import java.util.List;
import com.hermitowo.advancedtfctech.common.component.ATTComponents;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import net.dries007.tfc.util.Helpers;

public class ATTClientForgeEvents
{
    public static void init()
    {
        final IEventBus bus = NeoForge.EVENT_BUS;

        bus.addListener(ATTClientForgeEvents::onTooltip);
    }

    private static void onTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final List<Component> text = event.getToolTip();
        if (!stack.isEmpty())
        {
            if (!ModList.get().isLoaded("firmalife"))
                if (Helpers.isItem(stack, ATTItems.PINEAPPLE_WINDED_PIRN.get()))
                    text.add(Component.translatable("advancedtfctech.tooltip.firmalife_not_loaded"));

            if (stack.getOrDefault(ATTComponents.MACHINE_MADE, false))
                text.add(Component.translatable("advancedtfctech.tooltip.machine_made"));
        }
    }
}
