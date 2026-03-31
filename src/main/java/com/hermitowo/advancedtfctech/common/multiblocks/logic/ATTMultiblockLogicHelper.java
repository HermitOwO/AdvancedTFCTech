package com.hermitowo.advancedtfctech.common.multiblocks.logic;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class ATTMultiblockLogicHelper
{
    public static <S extends IMultiblockState & ProcessContext<?>> void sort(S state, int firstSlot, int slotCount)
    {
        IItemHandlerModifiable inventory = state.getInventory();
        for (int i = firstSlot; i < firstSlot + slotCount; i++)
        {
            for (int j = i + 1; j < firstSlot + slotCount; j++)
            {
                ItemStack holder1 = inventory.getStackInSlot(i).copy();
                ItemStack holder2 = inventory.getStackInSlot(j).copy();
                if (ItemStack.isSameItemSameComponents(holder1, holder2))
                {
                    int size1 = holder1.getCount();
                    int size2 = holder2.getCount();
                    int sizeMax = Math.min(inventory.getSlotLimit(i), holder1.getMaxStackSize());
                    if (size1 == sizeMax)
                        continue;
                    if (size1 + size2 > sizeMax)
                    {
                        int amount = sizeMax - size1;
                        inventory.getStackInSlot(i).grow(amount);
                        inventory.getStackInSlot(j).shrink(amount);
                    }
                    else
                    {
                        ItemStack stack = holder1.copyWithCount(size1 + size2);
                        inventory.setStackInSlot(i, stack);
                        inventory.setStackInSlot(j, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public static <S extends IMultiblockState & ProcessContext<?>> void handleItemOutput(S state, Supplier<@Nullable IItemHandler> output, int[] outputSlots)
    {
        IItemHandler outputHandler = output.get();
        if (outputHandler != null)
        {
            for (int i = outputSlots.length - 1; i >= 0; i--)
            {
                int slot = outputSlots[i];
                ItemStack extracted = state.getInventory().extractItem(slot, 1, true);
                if (!extracted.isEmpty())
                {
                    ItemStack remainder = ItemHandlerHelper.insertItem(outputHandler, extracted, false);

                    int successfullyMoved = extracted.getCount() - remainder.getCount();
                    if (successfullyMoved > 0)
                        state.getInventory().extractItem(slot, extracted.getCount() - remainder.getCount(), false);

                    // If there is no remainder, take from the next "from" slot.
                    if (remainder.getCount() <= 0)
                        break;
                }
            }
        }
    }
}
