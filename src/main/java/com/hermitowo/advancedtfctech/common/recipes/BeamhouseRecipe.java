package com.hermitowo.advancedtfctech.common.recipes;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import blusunrize.immersiveengineering.api.utils.codec.IEDualCodecs;
import com.google.common.collect.Lists;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import com.hermitowo.advancedtfctech.util.ATTDualCodecs;
import com.hermitowo.advancedtfctech.util.ModifiableSupplier;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class BeamhouseRecipe extends ATTMultiblockRecipe implements IItemStackProviderMultiblockRecipe
{
    public static final CachedRecipeList<BeamhouseRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.BEAMHOUSE);
    public static final ModifiableSupplier<RecipeMultiplier> MULTIPLIERS = ModifiableSupplier.of();

    public final IngredientWithSize input;
    public final SizedFluidIngredient fluidInput;
    public final ItemStackProvider output;

    public BeamhouseRecipe(ItemStackProvider output, IngredientWithSize input, SizedFluidIngredient fluidInput, int time, int energy)
    {
        super(TagOutput.EMPTY, ATTRecipeTypes.BEAMHOUSE, time, energy, MULTIPLIERS);
        this.output = output;
        this.input = input;
        this.fluidInput = fluidInput;

        setInputListWithSizes(Lists.newArrayList(this.input));
        this.fluidInputList = Lists.newArrayList(this.fluidInput);
        this.providerList = Lists.newArrayList(this.output);
    }

    public static RecipeHolder<BeamhouseRecipe> findRecipe(Level level, ItemStack stack, FluidStack fluid)
    {
        if (stack.isEmpty() || fluid.isEmpty())
            return null;
        for (RecipeHolder<BeamhouseRecipe> recipe : RECIPES.getRecipes(level))
            if (recipe.value().matches(stack, fluid))
                return recipe;
        return null;
    }

    public boolean matches(ItemStack stack, FluidStack fluid)
    {
        return isValidInput(stack) && isValidFluidInput(fluid);
    }

    public boolean isValidInput(ItemStack stack)
    {
        return this.input != null && this.input.test(stack);
    }

    public static boolean isValidRecipeInput(Level level, ItemStack stack)
    {
        for (RecipeHolder<BeamhouseRecipe> recipe : RECIPES.getRecipes(level))
            if (recipe.value().isValidInput(stack))
                return true;
        return false;
    }

    public boolean isValidFluidInput(FluidStack fluid)
    {
        return this.fluidInput != null && this.fluidInput.test(fluid);
    }

    @Override
    public int getMultipleProcessTicks()
    {
        return 0;
    }

    @Override
    protected IERecipeSerializer<BeamhouseRecipe> getIESerializer()
    {
        return ATTRecipeSerializers.BEAMHOUSE_SERIALIZER.get();
    }

    @Override
    public NonNullList<ItemStack> generateActualOutput(ItemStack input)
    {
        NonNullList<ItemStack> actualOutput = NonNullList.withSize(providerList.size(), ItemStack.EMPTY);
        for (int i = 0; i < providerList.size(); ++i)
        {
            ItemStackProvider provider = providerList.get(i);
            actualOutput.set(i, provider.getSingleStack(input));
        }
        return actualOutput;
    }

    public static class Serializer extends IERecipeSerializer<BeamhouseRecipe>
    {
        public static final DualMapCodec<RegistryFriendlyByteBuf, BeamhouseRecipe> CODECS = DualCompositeMapCodecs.composite(
            ATTDualCodecs.ITEM_STACK_PROVIDER.fieldOf("result"), r -> r.output,
            IngredientWithSize.CODECS.fieldOf("input"), r -> r.input,
            IEDualCodecs.SIZED_FLUID_INGREDIENT.fieldOf("fluid"), r -> r.fluidInput,
            DualCodecs.INT.fieldOf("time"), MultiblockRecipe::getBaseTime,
            DualCodecs.INT.fieldOf("energy"), MultiblockRecipe::getBaseEnergy,
            BeamhouseRecipe::new
        );

        @Override
        protected DualMapCodec<RegistryFriendlyByteBuf, BeamhouseRecipe> codecs()
        {
            return CODECS;
        }

        @Override
        public ItemStack getIcon()
        {
            return ATTMultiblockLogic.BEAMHOUSE.iconStack();
        }
    }
}
