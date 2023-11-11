package com.hermitowo.advancedtfctech.common.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

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
                if (ItemHandlerHelper.canItemStacksStack(holder1, holder2))
                {
                    int size1 = holder1.getCount();
                    int size2 = holder2.getCount();
                    int sizeMax = holder1.getMaxStackSize();
                    if (size1 == sizeMax || size2 == sizeMax)
                        continue;
                    if (size1 + size2 > sizeMax)
                    {
                        if (size1 >= size2)
                        {
                            int amount = sizeMax - size2;
                            inventory.getStackInSlot(i).shrink(amount);
                            inventory.getStackInSlot(j).grow(amount);
                        }
                        else
                        {
                            int amount = sizeMax - size1;
                            inventory.getStackInSlot(i).grow(amount);
                            inventory.getStackInSlot(j).shrink(amount);
                        }
                    }
                    else
                    {
                        ItemStack stack = ItemHandlerHelper.copyStackWithSize(holder1, size1 + size2);
                        inventory.setStackInSlot(i, stack);
                        inventory.setStackInSlot(j, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public static <S extends IMultiblockState & ProcessContext<?>> void handleItemOutput(S state, CapabilityReference<IItemHandler> output, int[] outputSlots)
    {
        IItemHandler outputHandler = output.getNullable();
        if (outputHandler != null)
        {
            for (int i : outputSlots)
            {
                final ItemStack nextStack = state.getInventory().getStackInSlot(i);
                if (nextStack.isEmpty())
                    continue;
                ItemStack stack = ItemHandlerHelper.copyStackWithSize(nextStack, 1);
                stack = ItemHandlerHelper.insertItem(outputHandler, stack, false);
                if (stack.isEmpty())
                    nextStack.shrink(1);
            }
        }
    }
}
