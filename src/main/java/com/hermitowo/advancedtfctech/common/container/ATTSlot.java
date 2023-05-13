package com.hermitowo.advancedtfctech.common.container;

import com.hermitowo.advancedtfctech.api.crafting.GristMillRecipe;
import com.hermitowo.advancedtfctech.api.crafting.ThresherRecipe;
import javax.annotation.Nonnull;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ATTSlot extends Slot
{
    final AbstractContainerMenu containerMenu;

    public ATTSlot(AbstractContainerMenu containerMenu, Container inv, int id, int xPosition, int yPosition)
    {
        super(inv, id, xPosition, yPosition);
        this.containerMenu = containerMenu;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return true;
    }

    public static class ItemOutput extends ATTSlot
    {
        public ItemOutput(AbstractContainerMenu container, Container inv, int id, int xPosition, int yPosition)
        {
            super(container, inv, id, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack)
        {
            return false;
        }
    }

    public static class ThresherInput extends ATTSlot
    {
        private final Level level;

        public ThresherInput(AbstractContainerMenu container, Container inv, int id, int x, int y, Level level)
        {
            super(container, inv, id, x, y);
            this.level = level;
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return !stack.isEmpty() && ThresherRecipe.isValidRecipeInput(level, stack);
        }
    }

    public static class GristMillInput extends ATTSlot
    {
        private final Level level;

        public GristMillInput(AbstractContainerMenu container, Container inv, int id, int x, int y, Level level)
        {
            super(container, inv, id, x, y);
            this.level = level;
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return !stack.isEmpty() && GristMillRecipe.isValidRecipeInput(level, stack);
        }
    }
}
