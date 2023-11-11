package com.hermitowo.advancedtfctech.common.recipes;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class BeamhouseRecipe extends ATTMultiblockRecipe implements IItemStackProviderMultiblockRecipe
{
    public static final CachedRecipeList<BeamhouseRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.BEAMHOUSE);

    public final IngredientWithSize input;
    public final FluidTagInput fluidInput;
    public final ItemStackProvider output;

    public BeamhouseRecipe(ResourceLocation id, ItemStackProvider output, IngredientWithSize input, FluidTagInput fluidInput, int time, int energy)
    {
        super(LAZY_EMPTY, ATTRecipeTypes.BEAMHOUSE, id);
        this.output = output;
        this.input = input;
        this.fluidInput = fluidInput;

        timeAndEnergy(time, energy);

        setInputListWithSizes(Lists.newArrayList(this.input));
        this.fluidInputList = Lists.newArrayList(this.fluidInput);
        this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.output.stack().get()));
        this.providerList = Lazy.of(() -> NonNullList.of(ItemStackProvider.empty(), this.output));
    }

    public static BeamhouseRecipe findRecipe(Level level, ItemStack stack, FluidStack fluid)
    {
        if (stack.isEmpty() || fluid.isEmpty())
            return null;
        for (BeamhouseRecipe recipe : RECIPES.getRecipes(level))
            if (recipe.matches(stack, fluid))
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
        for (BeamhouseRecipe recipe : RECIPES.getRecipes(level))
            if (recipe != null && recipe.isValidInput(stack))
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
        NonNullList<ItemStack> actualOutput = NonNullList.withSize(outputList.get().size(), ItemStack.EMPTY);
        for (int i = 0; i < outputList.get().size(); ++i)
        {
            ItemStackProvider provider = providerList.get().get(i);
            actualOutput.set(i, provider.getStack(input));
        }
        return actualOutput;
    }

    public static class Serializer extends IERecipeSerializer<BeamhouseRecipe>
    {
        @Override
        public ItemStack getIcon()
        {
            return ATTMultiblockLogic.BEAMHOUSE.iconStack();
        }

        @Override
        public BeamhouseRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context)
        {
            ItemStackProvider output = ItemStackProvider.fromJson(GsonHelper.getAsJsonObject(json, "result"));
            IngredientWithSize input = IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "input"));
            FluidTagInput fluidInput = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "fluid"));
            int time = GsonHelper.getAsInt(json, "time");
            int energy = GsonHelper.getAsInt(json, "energy");

            return ATTConfig.SERVER.beamhouseConfig.apply(new BeamhouseRecipe(recipeId, output, input, fluidInput, time, energy));
        }

        @Nullable
        @Override
        public BeamhouseRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            ItemStackProvider output = ItemStackProvider.fromNetwork(buffer);
            IngredientWithSize input = IngredientWithSize.read(buffer);
            FluidTagInput fluidInput = FluidTagInput.read(buffer);
            int time = buffer.readInt();
            int energy = buffer.readInt();

            return new BeamhouseRecipe(recipeId, output, input, fluidInput, time, energy);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BeamhouseRecipe recipe)
        {
            recipe.output.toNetwork(buffer);
            recipe.input.write(buffer);
            recipe.fluidInput.write(buffer);
            buffer.writeInt(recipe.getTotalProcessTime());
            buffer.writeInt(recipe.getTotalProcessEnergy());
        }
    }
}
