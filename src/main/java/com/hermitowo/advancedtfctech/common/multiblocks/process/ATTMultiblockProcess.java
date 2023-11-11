package com.hermitowo.advancedtfctech.common.multiblocks.process;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.utils.IngredientUtils;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import com.hermitowo.advancedtfctech.common.recipes.ATTMultiblockRecipe;
import com.hermitowo.advancedtfctech.common.recipes.IItemStackProviderMultiblockRecipe;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * {@link blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine} + part of
 * {@link blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInWorld}
 * I don't want to do this but I need to.
 */
@SuppressWarnings("unused")
public class ATTMultiblockProcess<R extends MultiblockRecipe> extends MultiblockProcess<R, ATTProcessContext<R>>
{
    protected final int[] inputSlots;
    protected int[] inputAmounts = null;
    protected int[] inputTanks = new int[0];

    public ATTMultiblockProcess(ResourceLocation recipeId, BiFunction<Level, ResourceLocation, R> getRecipe, int... inputSlots)
    {
        super(recipeId, getRecipe);
        this.inputSlots = inputSlots;
    }

    public ATTMultiblockProcess(R recipe, int... inputSlots)
    {
        super(recipe);
        this.inputSlots = inputSlots;
    }

    public ATTMultiblockProcess(BiFunction<Level, ResourceLocation, R> getRecipe, CompoundTag data)
    {
        super(getRecipe, data);
        this.inputSlots = data.getIntArray("process_inputSlots");
        setInputAmounts(data.getIntArray("process_inputAmounts"));
        setInputTanks(data.getIntArray("process_inputTanks"));
    }

    public ATTMultiblockProcess<R> setInputTanks(int... inputTanks)
    {
        this.inputTanks = inputTanks;
        return this;
    }

    public ATTMultiblockProcess<R> setInputAmounts(int... inputAmounts)
    {
        this.inputAmounts = inputAmounts;
        return this;
    }

    public int[] getInputSlots()
    {
        return this.inputSlots;
    }

    @Nullable
    public int[] getInputAmounts()
    {
        return this.inputAmounts;
    }

    public int[] getInputTanks()
    {
        return this.inputTanks;
    }

    protected List<IngredientWithSize> getRecipeItemInputs(ATTProcessContext<R> context, Level level)
    {
        R recipe = getLevelData(level).recipe();
        return recipe == null ? List.of() : recipe.getItemInputs();
    }

    protected List<FluidTagInput> getRecipeFluidInputs(ATTProcessContext<R> context, Level level)
    {
        R recipe = getLevelData(level).recipe();
        return recipe == null ? List.of() : recipe.getFluidInputs();
    }

    @Override
    protected boolean canOutputItem(ATTProcessContext<R> context, ItemStack output)
    {
        int[] outputSlots = context.getOutputSlots();
        for (int iOutputSlot : outputSlots)
        {
            final IItemHandlerModifiable inv = context.getInventory();
            ItemStack s = inv.getStackInSlot(iOutputSlot);
            if (s.isEmpty())
                return true;
            final boolean match = ItemHandlerHelper.canItemStacksStack(s, output);
            if (match && s.getCount() + output.getCount() <= inv.getSlotLimit(iOutputSlot))
                return true;
        }
        return false;
    }

    @Override
    protected boolean canOutputFluid(ATTProcessContext<R> context, FluidStack output)
    {
        IFluidTank[] tanks = context.getInternalTanks();
        int[] outputTanks = context.getOutputTanks();
        for (int iOutputTank : outputTanks)
            if (tanks[iOutputTank].fill(output, IFluidHandler.FluidAction.SIMULATE) == output.getAmount())
                return true;
        return false;
    }

    @Override
    protected void outputFluid(ATTProcessContext<R> context, FluidStack output)
    {
        IFluidTank[] tanks = context.getInternalTanks();
        int[] outputTanks = context.getOutputTanks();
        for (int iOutputTank : outputTanks)
            if (tanks[iOutputTank].fill(output, IFluidHandler.FluidAction.SIMULATE) == output.getAmount())
            {
                tanks[iOutputTank].fill(output, IFluidHandler.FluidAction.EXECUTE);
                break;
            }
    }

    @Override
    protected void outputItem(ATTProcessContext<R> context, ItemStack output, IMultiblockLevel level)
    {
        R recipe = getRecipe(level.getRawLevel());
        if (recipe instanceof ATTMultiblockRecipe attRecipe)
            if (!attRecipe.getSecondaryOutputs().isEmpty())
                for (Lazy<ItemStack> secondaryOutput : attRecipe.getSecondaryOutputs())
                    context.doProcessOutput(secondaryOutput.get(), level);
        int[] outputSlots = context.getOutputSlots();
        for (int iOutputSlot : outputSlots)
        {
            final IItemHandlerModifiable inv = context.getInventory();
            ItemStack s = inv.getStackInSlot(iOutputSlot);
            if (s.isEmpty())
            {
                inv.setStackInSlot(iOutputSlot, output.copy());
                break;
            }
            else if (ItemHandlerHelper.canItemStacksStack(s, output) && s.getCount() + output.getCount() <= inv.getSlotLimit(iOutputSlot))
            {
                s.grow(output.getCount());
                break;
            }
        }
    }

    @Override
    public void doProcessTick(ATTProcessContext<R> context, IMultiblockLevel level)
    {
        R recipe = getLevelData(level.getRawLevel()).recipe();
        if (recipe == null)
            return;
        IItemHandlerModifiable inv = context.getInventory();
        if (recipe.shouldCheckItemAvailability() && recipe.getItemInputs() != null && inv != null)
        {
            NonNullList<ItemStack> query = NonNullList.withSize(inputSlots.length, ItemStack.EMPTY);
            for (int i = 0; i < inputSlots.length; i++)
                if (inputSlots[i] >= 0 && inputSlots[i] < inv.getSlots())
                    query.set(i, context.getInventory().getStackInSlot(inputSlots[i]));
            if (!IngredientUtils.stacksMatchIngredientWithSizeList(recipe.getItemInputs(), query))
            {
                this.clearProcess = true;
                return;
            }
        }
        super.doProcessTick(context, level);
    }

    @Override
    protected void processFinish(ATTProcessContext<R> context, IMultiblockLevel level)
    {
        super.processFinish(context, level);
        IItemHandlerModifiable inv = context.getInventory();
        List<IngredientWithSize> itemInputList = this.getRecipeItemInputs(context, level.getRawLevel());
        if (inv != null && this.inputSlots != null && itemInputList != null)
        {
            if (this.inputAmounts != null && this.inputSlots.length == this.inputAmounts.length)
            {
                for (int i = 0; i < this.inputSlots.length; i++)
                    if (this.inputAmounts[i] > 0)
                        inv.getStackInSlot(this.inputSlots[i]).shrink(this.inputAmounts[i]);

            }
            else
                for (IngredientWithSize ingr : new ArrayList<>(itemInputList))
                {
                    int ingrSize = ingr.getCount();
                    for (int slot : this.inputSlots)
                        if (!inv.getStackInSlot(slot).isEmpty() && ingr.test(inv.getStackInSlot(slot)))
                        {
                            int taken = Math.min(inv.getStackInSlot(slot).getCount(), ingrSize);
                            inv.getStackInSlot(slot).shrink(taken);
                            if (inv.getStackInSlot(slot).getCount() <= 0)
                                inv.setStackInSlot(slot, ItemStack.EMPTY);
                            if ((ingrSize -= taken) <= 0)
                                break;
                        }
                }
        }
        IFluidTank[] tanks = context.getInternalTanks();
        List<FluidTagInput> fluidInputList = this.getRecipeFluidInputs(context, level.getRawLevel());
        if (tanks != null && this.inputTanks != null && fluidInputList != null)
        {
            for (FluidTagInput ingr : new ArrayList<>(fluidInputList))
            {
                int ingrSize = ingr.getAmount();
                for (int tank : this.inputTanks)
                    if (tanks[tank] != null && ingr.testIgnoringAmount(tanks[tank].getFluid()))
                    {
                        int taken = Math.min(tanks[tank].getFluidAmount(), ingrSize);
                        tanks[tank].drain(taken, IFluidHandler.FluidAction.EXECUTE);
                        if ((ingrSize -= taken) <= 0)
                            break;
                    }
            }
        }
    }

    @Override
    public void writeExtraDataToNBT(CompoundTag nbt)
    {
        if (inputSlots != null)
            nbt.putIntArray("process_inputSlots", inputSlots);
        if (inputAmounts != null)
            nbt.putIntArray("process_inputAmounts", inputAmounts);
        if (inputTanks != null)
            nbt.putIntArray("process_inputTanks", inputTanks);
    }

    public static class ProcessWithItemStackProvider<R extends ATTMultiblockRecipe & IItemStackProviderMultiblockRecipe> extends ATTMultiblockProcess<R>
    {
        public ProcessWithItemStackProvider(R recipe, int... inputSlots)
        {
            super(recipe, inputSlots);
        }

        public ProcessWithItemStackProvider(BiFunction<Level, ResourceLocation, R> getRecipe, CompoundTag data)
        {
            super(getRecipe, data);
        }

        @Override
        protected List<ItemStack> getRecipeItemOutputs(Level level, ATTProcessContext<R> context)
        {
            R recipe = getRecipe(level);
            if (recipe == null)
                return NonNullList.create();
            ItemStack input = context.getInventory().getStackInSlot(this.inputSlots[0]);
            return recipe.generateActualOutput(input);
        }
    }
}
