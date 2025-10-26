package com.hermitowo.advancedtfctech.common.recipes;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.util.ATTDualCodecs;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class FleshingMachineRecipe extends IESerializableRecipe
{
    public static final CachedRecipeList<FleshingMachineRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.FLESHING_MACHINE);

    public final ItemStackProvider output;
    public final Ingredient input;
    public final int time;
    public final int energy;

    public FleshingMachineRecipe(ItemStackProvider output, Ingredient input, int time, int energy)
    {
        super(TagOutput.EMPTY, ATTRecipeTypes.FLESHING_MACHINE);
        this.output = output;
        this.input = input;
        this.time = time;
        this.energy = energy;
    }

    public static FleshingMachineRecipe findRecipe(Level level, ItemStack stack, @Nullable FleshingMachineRecipe hint)
    {
        if (stack.isEmpty())
            return null;
        if (hint != null && hint.isValidInput(stack))
            return hint;
        for (RecipeHolder<FleshingMachineRecipe> recipe : RECIPES.getRecipes(level))
            if (recipe.value().input.test(stack))
                return recipe.value();
        return null;
    }

    public static FleshingMachineRecipe findRecipe(Level level, ItemStack stack)
    {
        return findRecipe(level, stack, null);
    }

    public boolean isValidInput(ItemStack stack)
    {
        return this.input != null && this.input.test(stack);
    }

    public static boolean isValidRecipeInput(Level level, ItemStack stack)
    {
        for (RecipeHolder<FleshingMachineRecipe> recipe : RECIPES.getRecipes(level))
            if (recipe.value().isValidInput(stack))
                return true;
        return false;
    }

    public int getTime()
    {
        return this.time;
    }

    public int getEnergy()
    {
        return this.energy;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider)
    {
        return this.output.getEmptyStack();
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer()
    {
        return ATTRecipeSerializers.FLESHING_MACHINE_SERIALIZER.get();
    }

    public static class Serializer extends IERecipeSerializer<FleshingMachineRecipe>
    {
        public static final DualMapCodec<RegistryFriendlyByteBuf, FleshingMachineRecipe> CODECS = DualCompositeMapCodecs.composite(
            ATTDualCodecs.ITEM_STACK_PROVIDER.fieldOf("result"), r -> r.output,
            DualCodecs.INGREDIENT.fieldOf("input"), r -> r.input,
            DualCodecs.INT.fieldOf("time"), r -> r.time,
            DualCodecs.INT.fieldOf("energy"), r -> r.energy,
            FleshingMachineRecipe::new
        );

        @Override
        protected DualMapCodec<RegistryFriendlyByteBuf, FleshingMachineRecipe> codecs()
        {
            return CODECS;
        }

        @Override
        public ItemStack getIcon()
        {
            return new ItemStack(ATTBlocks.FLESHING_MACHINE.get());
        }
    }
}
