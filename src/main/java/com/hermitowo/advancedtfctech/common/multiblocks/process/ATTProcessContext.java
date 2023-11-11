package com.hermitowo.advancedtfctech.common.multiblocks.process;

import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * {@link blusunrize.immersiveengineering.common.blocks.multiblocks.process.ProcessContext}
 * I don't want to do this but I need to.
 */
@SuppressWarnings("unused")
public interface ATTProcessContext<R extends MultiblockRecipe> extends ProcessContext<R>
{
    default void doProcessOutput(ItemStack result, IMultiblockLevel level)
    {
    }

    default void doProcessFluidOutput(FluidStack output)
    {
    }

    default int[] getOutputTanks()
    {
        return EMPTY_INTS;
    }

    default int[] getOutputSlots()
    {
        return EMPTY_INTS;
    }

    int[] EMPTY_INTS = {};
}
