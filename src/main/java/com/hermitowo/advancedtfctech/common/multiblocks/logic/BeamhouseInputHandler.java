package com.hermitowo.advancedtfctech.common.multiblocks.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import static com.hermitowo.advancedtfctech.common.multiblocks.logic.BeamhouseLogic.*;

/**
 * {@link blusunrize.immersiveengineering.common.blocks.multiblocks.logic.arcfurnace.ArcFurnaceInputHandler}
 */
public class BeamhouseInputHandler implements IItemHandler
{
    private final IItemHandlerModifiable wrapped;
    private final Runnable onChanged;
    private final Supplier<@Nullable Level> getLevel;

    public BeamhouseInputHandler(IItemHandlerModifiable wrapped, Runnable onChanged, Supplier<@Nullable Level> getLevel)
    {
        this.wrapped = wrapped;
        this.onChanged = onChanged;
        this.getLevel = getLevel;
    }

    @Override
    public int getSlots()
    {
        return 17;
    }

    @Override
    public @Nonnull ItemStack getStackInSlot(int slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return stack;
        boolean b = isItemValid(slot, stack);
        if (!b)
            return stack;
        stack = stack.copy();
        List<Integer> possibleSlots = new ArrayList<>(IN_SLOT_COUNT);
        for (int i = FIRST_IN_SLOT; i < IN_SLOT_COUNT; i++)
        {
            ItemStack here = wrapped.getStackInSlot(i);
            if (here.isEmpty())
            {
                if (!simulate)
                    wrapped.setStackInSlot(i, stack);
                onChanged.run();
                return ItemStack.EMPTY;
            }
            else if (ItemHandlerHelper.canItemStacksStack(stack, here) && here.getCount() < here.getMaxStackSize())
                possibleSlots.add(i);
        }
        possibleSlots.sort(Comparator.comparingInt(a -> wrapped.getStackInSlot(a).getCount()));
        for (int i : possibleSlots)
        {
            ItemStack here = wrapped.getStackInSlot(i);
            int fillCount = Math.min(here.getMaxStackSize() - here.getCount(), stack.getCount());
            if (!simulate)
                here.grow(fillCount);
            stack.shrink(fillCount);
            if (stack.isEmpty())
            {
                onChanged.run();
                return ItemStack.EMPTY;
            }
        }
        onChanged.run();
        return stack;
    }

    @Override
    public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return BeamhouseRecipe.isValidRecipeInput(getLevel.get(), stack);
    }
}
