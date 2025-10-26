package com.hermitowo.advancedtfctech.common.recipes;

import java.util.List;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import blusunrize.immersiveengineering.api.crafting.TagOutputList;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.google.common.collect.Lists;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import com.hermitowo.advancedtfctech.util.ModifiableSupplier;
import malte0811.dualcodecs.DualCodecs;
import malte0811.dualcodecs.DualCompositeMapCodecs;
import malte0811.dualcodecs.DualMapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class PowerLoomRecipe extends ATTMultiblockRecipe
{
    public static final CachedRecipeList<PowerLoomRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.POWER_LOOM);
    public static final ModifiableSupplier<RecipeMultiplier> MULTIPLIERS = ModifiableSupplier.of();

    public final List<IngredientWithSize> inputs;
    public final IngredientWithSize secondaryInput;
    public final TagOutput output;
    public final ItemStack secondaryOutput;
    public final ResourceLocation inProgressTexture;

    public PowerLoomRecipe(TagOutput output, ItemStack secondaryOutput, List<IngredientWithSize> inputs, IngredientWithSize secondaryInput, ResourceLocation inProgressTexture, int time, int energy)
    {
        super(output, ATTRecipeTypes.POWER_LOOM, time, energy, MULTIPLIERS);
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.inputs = inputs;
        this.secondaryInput = secondaryInput;
        this.inProgressTexture = inProgressTexture;

        setInputListWithSizes(Lists.newArrayList(this.inputs));
        this.outputList = new TagOutputList(output);
    }

    @Override
    public ItemStack getSecondaryOutput()
    {
        return secondaryOutput;
    }

    public static RecipeHolder<PowerLoomRecipe> findRecipe(Level level, ItemStack pirn, ItemStack weave)
    {
        if (pirn.isEmpty() || weave.isEmpty())
            return null;
        for (RecipeHolder<PowerLoomRecipe> recipe : RECIPES.getRecipes(level))
            if (recipe.value().matches(pirn, weave))
                return recipe;
        return null;
    }

    public static RecipeHolder<PowerLoomRecipe> findRecipeForRendering(Level level, ItemStack secondaryInput)
    {
        if (secondaryInput.isEmpty())
            return null;
        for (RecipeHolder<PowerLoomRecipe> recipe : RECIPES.getRecipes(level))
            if (recipe.value().isValidSecondaryInput(secondaryInput))
                return recipe;
        return null;
    }

    public boolean matches(ItemStack pirn, ItemStack weave)
    {
        return isValidPirn(pirn) && isValidWeaveWithSize(weave);
    }

    public boolean isValidPirn(ItemStack stack)
    {
        return this.inputs.get(1) != null && this.inputs.get(1).test(stack);
    }

    public static boolean isValidPirnInput(Level level, ItemStack stack)
    {
        for (RecipeHolder<PowerLoomRecipe> recipe : RECIPES.getRecipes(level))
            if (recipe.value().isValidPirn(stack))
                return true;
        return false;
    }

    public boolean isValidWeave(ItemStack stack)
    {
        return this.inputs.get(0) != null && this.inputs.get(0).testIgnoringSize(stack);
    }

    public boolean isValidWeaveWithSize(ItemStack stack)
    {
        return this.inputs.get(0) != null && this.inputs.get(0).test(stack);
    }

    public static boolean isValidWeaveInput(Level level, ItemStack stack)
    {
        for (RecipeHolder<PowerLoomRecipe> recipe : RECIPES.getRecipes(level))
            if (recipe.value().isValidWeave(stack))
                return true;
        return false;
    }

    public boolean isValidSecondaryInput(ItemStack stack)
    {
        return this.secondaryInput != null && this.secondaryInput.testIgnoringSize(stack);
    }

    @Override
    public int getMultipleProcessTicks()
    {
        return 0;
    }

    @Override
    protected IERecipeSerializer<PowerLoomRecipe> getIESerializer()
    {
        return ATTRecipeSerializers.POWER_LOOM_SERIALIZER.get();
    }

    public static class Serializer extends IERecipeSerializer<PowerLoomRecipe>
    {
        public static final DualMapCodec<RegistryFriendlyByteBuf, PowerLoomRecipe> CODECS = DualCompositeMapCodecs.composite(
            TagOutput.CODECS.fieldOf("result"), r -> r.output,
            DualCodecs.ITEM_STACK.optionalFieldOf("secondary_output", ItemStack.EMPTY), r -> r.secondaryOutput,
            IngredientWithSize.CODECS.listOf().fieldOf("inputs"), r -> r.inputs,
            IngredientWithSize.CODECS.fieldOf("secondary_input"), r -> r.secondaryInput,
            DualCodecs.RESOURCE_LOCATION.fieldOf("in_progress_texture"), r -> r.inProgressTexture,
            DualCodecs.INT.fieldOf("time"), MultiblockRecipe::getBaseTime,
            DualCodecs.INT.fieldOf("energy"), MultiblockRecipe::getBaseEnergy,
            PowerLoomRecipe::new
        );

        @Override
        protected DualMapCodec<RegistryFriendlyByteBuf, PowerLoomRecipe> codecs()
        {
            return CODECS;
        }

        @Override
        public ItemStack getIcon()
        {
            return ATTMultiblockLogic.POWER_LOOM.iconStack();
        }
    }
}
