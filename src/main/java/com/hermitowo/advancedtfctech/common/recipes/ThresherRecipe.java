package com.hermitowo.advancedtfctech.common.recipes;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
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

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class ThresherRecipe extends ATTMultiblockRecipe implements IItemStackProviderMultiblockRecipe
{
    public static final CachedRecipeList<ThresherRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.THRESHER);
    public static final ModifiableSupplier<RecipeMultiplier> MULTIPLIERS = ModifiableSupplier.of();

    public final IngredientWithSize input;
    public final ItemStackProvider output;
    public final ItemStack secondaryOutput;

    public ThresherRecipe(ItemStackProvider output, IngredientWithSize input, ItemStack secondaryOutput, int time, int energy)
    {
        super(TagOutput.EMPTY, ATTRecipeTypes.THRESHER, time, energy, MULTIPLIERS);
        this.output = output;
        this.input = input;
        this.secondaryOutput = secondaryOutput;

        setInputListWithSizes(Lists.newArrayList(this.input));
        this.providerList = Lists.newArrayList(this.output);
    }

    @Override
    public ItemStack getSecondaryOutput()
    {
        return secondaryOutput;
    }

    public static RecipeHolder<ThresherRecipe> findRecipe(Level level, ItemStack stack)
    {
        for (RecipeHolder<ThresherRecipe> recipe : RECIPES.getRecipes(level))
            if (recipe.value().input.test(stack))
                return recipe;
        return null;
    }

    @Override
    public int getMultipleProcessTicks()
    {
        return 0;
    }

    @Override
    protected IERecipeSerializer<ThresherRecipe> getIESerializer()
    {
        return ATTRecipeSerializers.THRESHER_SERIALIZER.get();
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

    public static class Serializer extends IERecipeSerializer<ThresherRecipe>
    {
        public static final DualMapCodec<RegistryFriendlyByteBuf, ThresherRecipe> CODECS = DualCompositeMapCodecs.composite(
            ATTDualCodecs.ITEM_STACK_PROVIDER.fieldOf("result"), r -> r.output,
            IngredientWithSize.CODECS.fieldOf("input"), r -> r.input,
            DualCodecs.ITEM_STACK.optionalFieldOf("secondary_output", ItemStack.EMPTY), r -> r.secondaryOutput,
            DualCodecs.INT.fieldOf("time"), MultiblockRecipe::getBaseTime,
            DualCodecs.INT.fieldOf("energy"), MultiblockRecipe::getBaseEnergy,
            ThresherRecipe::new
        );

        @Override
        protected DualMapCodec<RegistryFriendlyByteBuf, ThresherRecipe> codecs()
        {
            return CODECS;
        }

        @Override
        public ItemStack getIcon()
        {
            return ATTMultiblockLogic.THRESHER.iconStack();
        }
    }
}
