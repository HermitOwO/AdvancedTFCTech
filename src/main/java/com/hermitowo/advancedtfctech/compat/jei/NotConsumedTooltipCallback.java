package com.hermitowo.advancedtfctech.compat.jei;

import java.util.List;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class NotConsumedTooltipCallback implements IRecipeSlotTooltipCallback
{
    @Override
    public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip)
    {
        tooltip.add(new TranslatableComponent("advancedtfctech.jei.not_consumed").withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.ITALIC));
    }
}
