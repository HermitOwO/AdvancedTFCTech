package com.hermitowo.advancedtfctech.common.container;

import java.util.List;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import static com.hermitowo.advancedtfctech.common.multiblocks.logic.PowerLoomLogic.*;

public class PowerLoomInventory extends ItemStackHandler
{
    private final List<SlotwiseItemHandler.IOConstraint> slotConstraints;
    private final Runnable onChanged;

    public PowerLoomInventory(List<SlotwiseItemHandler.IOConstraint> slotConstraints, Runnable onChanged)
    {
        super(slotConstraints.size());
        this.slotConstraints = slotConstraints;
        this.onChanged = onChanged;
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);
        onChanged.run();
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if (slot >= this.slotConstraints.size() || !this.slotConstraints.get(slot).allowInsert().test(stack))
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (slot >= this.slotConstraints.size() || !this.slotConstraints.get(slot).allowExtract())
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        if (slot >= FIRST_PIRN_IN_SLOT && slot < PIRN_IN_SLOT_COUNT)
            return 1;
        return 64;
    }
}
