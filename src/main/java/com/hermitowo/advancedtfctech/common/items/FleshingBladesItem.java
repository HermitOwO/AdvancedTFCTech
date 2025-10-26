package com.hermitowo.advancedtfctech.common.items;

import java.util.List;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

@ParametersAreNonnullByDefault
public class FleshingBladesItem extends Item
{
    public FleshingBladesItem()
    {
        super(new Properties().component(DataComponents.MAX_DAMAGE, 10));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag)
    {
        float integrity = 1 - getDamage(stack) / (float) getMaxDamage(stack);
        list.add(Component.translatable("desc.advancedtfctech.bladeIntegrity", String.format("%.2f", 100 * integrity)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isDamageable(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack)
    {
        return ATTConfig.SERVER.fleshingMachine_bladesDamage.get();
    }
}
