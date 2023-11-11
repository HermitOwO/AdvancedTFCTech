package com.hermitowo.advancedtfctech.common.items;

import java.util.List;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
public class FleshingBladesItem extends Item
{
    public FleshingBladesItem()
    {
        super(new Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag)
    {
        float integrity = getRelativeBarWidth(stack) * 100F;
        list.add(Component.translatable("desc.advancedtfctech.bladeIntegrity", String.format("%.2f", integrity)));
    }

    @Override
    public int getBarWidth(ItemStack stack)
    {
        return Math.round(MAX_BAR_WIDTH * getRelativeBarWidth(stack));
    }

    private float getRelativeBarWidth(ItemStack stack)
    {
        return 1 - ItemNBTHelper.getInt(stack, "bladeDmg") / (float) ATTConfig.SERVER.fleshingMachine_bladesDamage.get();
    }

    @Override
    public int getBarColor(ItemStack stack)
    {
        return Mth.hsvToRgb(Math.max(0.0F, getBarWidth(stack) / (float) MAX_BAR_WIDTH) / 3.0F, 1.0F, 1.0F);
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

    @Override
    public boolean isDamaged(ItemStack stack)
    {
        return ItemNBTHelper.getInt(stack, "bladeDmg") > 0;
    }

    @Override
    public int getDamage(ItemStack stack)
    {
        return ItemNBTHelper.getInt(stack, "bladeDmg");
    }

    @Override
    public void setDamage(ItemStack stack, int damage)
    {
        ItemNBTHelper.putInt(stack, "bladeDmg", damage);
    }
}
