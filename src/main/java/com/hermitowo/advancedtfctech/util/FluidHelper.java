package com.hermitowo.advancedtfctech.util;

import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import javax.annotation.Nonnull;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

public class FluidHelper
{
    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, IFluidHandler handler, boolean canExtract)
    {
        Mutable<FluidStack> lastNonSimulated = new MutableObject<>();
        MutableBoolean isInsert = new MutableBoolean();
        IFluidHandler simulationWrapper = new FluidUtils.WrapperFluidHandler(handler)
        {
            @Override
            public int fill(FluidStack resource, FluidAction action)
            {
                int result = handler.fill(resource, FluidAction.SIMULATE);
                if (action == FluidAction.EXECUTE)
                {
                    lastNonSimulated.setValue(resource.copyWithAmount(result));
                    isInsert.setTrue();
                }
                return result;
            }

            @Nonnull
            @Override
            public FluidStack drain(FluidStack resource, FluidAction action)
            {
                FluidStack result = handler.drain(resource, FluidAction.SIMULATE);
                if (action == FluidAction.EXECUTE)
                {
                    isInsert.setFalse();
                    lastNonSimulated.setValue(result.copy());
                }
                return result;
            }

            @Nonnull
            @Override
            public FluidStack drain(int maxDrain, FluidAction action)
            {
                FluidStack result = handler.drain(maxDrain, FluidAction.SIMULATE);
                if (action == FluidAction.EXECUTE)
                {
                    isInsert.setFalse();
                    lastNonSimulated.setValue(result.copy());
                }
                return result;
            }
        };

        final boolean success = FluidUtil.interactWithFluidHandler(player, hand, simulationWrapper);
        if (success)
        {
            if (isInsert.booleanValue())
                handler.fill(lastNonSimulated.getValue(), IFluidHandler.FluidAction.EXECUTE);
            else if (canExtract)
                handler.drain(lastNonSimulated.getValue(), IFluidHandler.FluidAction.EXECUTE);
        }
        return success;
    }

    public static boolean drainFluidContainer(IItemHandlerModifiable inventory, FluidTank tank, int inputSlot, int outputSlot)
    {
        int amountPrev = tank.getFluidAmount();
        ItemStack outputStack = inventory.getStackInSlot(outputSlot);
        ItemStack emptyContainer = drainFluidContainer(tank, inventory.getStackInSlot(inputSlot), outputStack);
        if (amountPrev != tank.getFluidAmount())
        {
            if (ItemStack.isSameItemSameComponents(outputStack, emptyContainer))
                outputStack.grow(emptyContainer.getCount());
            else if (outputStack.isEmpty())
                inventory.setStackInSlot(outputSlot, emptyContainer.copy());
            inventory.getStackInSlot(inputSlot).shrink(outputSlot);
            return true;
        }
        else
            return false;
    }

    public static ItemStack drainFluidContainer(IFluidHandler handler, ItemStack containerIn, ItemStack containerOut)
    {
        FluidActionResult result = FluidUtils.tryEmptyContainer(containerIn, handler, Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (result.isSuccess())
        {
            ItemStack empty = result.getResult();
            if ((containerOut.isEmpty() || ItemStack.isSameItemSameComponents(containerOut, empty)))
            {
                if (!containerOut.isEmpty() && containerOut.getCount() + empty.getCount() > containerOut.getMaxStackSize())
                    return ItemStack.EMPTY;
                result = FluidUtils.tryEmptyContainer(containerIn, handler, Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                if (result.isSuccess())
                {
                    return result.getResult();
                }
            }
        }
        return ItemStack.EMPTY;

    }
}
