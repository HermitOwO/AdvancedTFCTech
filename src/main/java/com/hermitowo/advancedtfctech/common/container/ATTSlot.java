package com.hermitowo.advancedtfctech.common.container;

import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import com.hermitowo.advancedtfctech.common.recipes.GristMillRecipe;
import com.hermitowo.advancedtfctech.common.recipes.ThresherRecipe;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@ParametersAreNonnullByDefault
public abstract class ATTSlot extends Slot
{
    final AbstractContainerMenu containerMenu;

    public ATTSlot(AbstractContainerMenu containerMenu, Container inv, int id, int xPosition, int yPosition)
    {
        super(inv, id, xPosition, yPosition);
        this.containerMenu = containerMenu;
    }

    public static class NotPlaceable extends ATTSlot
    {
        public NotPlaceable(AbstractContainerMenu container, Container inv, int id, int xPosition, int yPosition)
        {
            super(container, inv, id, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack)
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

    public static class BeamhouseInput extends ATTSlot
    {
        private final Level level;

        public BeamhouseInput(AbstractContainerMenu container, Container inv, int id, int x, int y, Level level)
        {
            super(container, inv, id, x, y);
            this.level = level;
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return !stack.isEmpty() && BeamhouseRecipe.isValidRecipeInput(level, stack);
        }
    }
}
