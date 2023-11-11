package com.hermitowo.advancedtfctech.util;

import blusunrize.immersiveengineering.api.fluid.FluidUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import javax.annotation.Nonnull;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
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
                    lastNonSimulated.setValue(new FluidStack(resource, result));
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
                handler.fill(lastNonSimulated.getValue(), FluidAction.EXECUTE);
            else if (canExtract)
                handler.drain(lastNonSimulated.getValue(), FluidAction.EXECUTE);
        }
        return success;
    }

    public static boolean drainFluidContainer(IItemHandlerModifiable inventory, FluidTank tank, int inputSlot, int outputSlot)
    {
        int amountPrev = tank.getFluidAmount();
        ItemStack outputStack = inventory.getStackInSlot(outputSlot);
        ItemStack emptyContainer = Utils.drainFluidContainer(tank, inventory.getStackInSlot(inputSlot), outputStack);
        if (amountPrev != tank.getFluidAmount())
        {
            if (ItemHandlerHelper.canItemStacksStack(outputStack, emptyContainer))
                outputStack.grow(emptyContainer.getCount());
            else if (outputStack.isEmpty())
                inventory.setStackInSlot(outputSlot, emptyContainer.copy());
            inventory.getStackInSlot(inputSlot).shrink(outputSlot);
            return true;
        }
        else
            return false;
    }
}
