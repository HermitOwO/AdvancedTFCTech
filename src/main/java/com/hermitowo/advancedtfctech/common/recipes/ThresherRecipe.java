package com.hermitowo.advancedtfctech.common.recipes;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.cache.CachedRecipeList;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
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

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class ThresherRecipe extends ATTMultiblockRecipe implements IItemStackProviderMultiblockRecipe
{
    public static final CachedRecipeList<ThresherRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.THRESHER);

    public final IngredientWithSize input;
    public final ItemStackProvider output;
    public final NonNullList<Lazy<ItemStack>> secondaryOutputs = NonNullList.create();

    public ThresherRecipe(ResourceLocation id, ItemStackProvider output, IngredientWithSize input, int time, int energy)
    {
        super(LAZY_EMPTY, ATTRecipeTypes.THRESHER, id);
        this.output = output;
        this.input = input;

        timeAndEnergy(time, energy);

        setInputListWithSizes(Lists.newArrayList(this.input));
        this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.output.stack().get()));
        this.providerList = Lazy.of(() -> NonNullList.of(ItemStackProvider.empty(), this.output));
    }

    public void addToSecondaryOutput(Lazy<ItemStack> output)
    {
        Preconditions.checkNotNull(output);
        secondaryOutputs.add(output);
    }

    @Override
    public NonNullList<Lazy<ItemStack>> getSecondaryOutputs()
    {
        return secondaryOutputs;
    }

    public static ThresherRecipe findRecipe(Level level, ItemStack stack)
    {
        for (ThresherRecipe recipe : RECIPES.getRecipes(level))
            if (recipe.input != null && recipe.input.test(stack))
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
        NonNullList<ItemStack> actualOutput = NonNullList.withSize(outputList.get().size(), ItemStack.EMPTY);
        for (int i = 0; i < outputList.get().size(); ++i)
        {
            ItemStackProvider provider = providerList.get().get(i);
            actualOutput.set(i, provider.getStack(input));
        }
        return actualOutput;
    }

    public static class Serializer extends IERecipeSerializer<ThresherRecipe>
    {
        @Override
        public ItemStack getIcon()
        {
            return ATTMultiblockLogic.THRESHER.iconStack();
        }

        @Override
        public ThresherRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context)
        {
            ItemStackProvider output = ItemStackProvider.fromJson(GsonHelper.getAsJsonObject(json, "result"));
            IngredientWithSize input = IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "input"));
            int time = GsonHelper.getAsInt(json, "time");
            int energy = GsonHelper.getAsInt(json, "energy");

            ThresherRecipe recipe = new ThresherRecipe(recipeId, output, input, time, energy);

            JsonArray array = json.getAsJsonArray("secondaries");
            for (int i = 0; i < array.size(); i++)
            {
                JsonObject element = array.get(i).getAsJsonObject();
                Lazy<ItemStack> stack = readOutput(element.get("output"));
                recipe.addToSecondaryOutput(stack);
            }

            return recipe;
        }

        @Nullable
        @Override
        public ThresherRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            ItemStackProvider output = ItemStackProvider.fromNetwork(buffer);
            IngredientWithSize input = IngredientWithSize.read(buffer);
            int time = buffer.readInt();
            int energy = buffer.readInt();

            ThresherRecipe recipe = ATTConfig.SERVER.thresherConfig.apply(new ThresherRecipe(recipeId, output, input, time, energy));

            int secondaryCount = buffer.readInt();
            for (int i = 0; i < secondaryCount; i++)
                recipe.addToSecondaryOutput(readLazyStack(buffer));

            return recipe;
        }

        public void toNetwork(FriendlyByteBuf buffer, ThresherRecipe recipe)
        {
            recipe.output.toNetwork(buffer);
            recipe.input.write(buffer);
            buffer.writeInt(recipe.getTotalProcessTime());
            buffer.writeInt(recipe.getTotalProcessEnergy());

            buffer.writeInt(recipe.secondaryOutputs.size());
            for (Lazy<ItemStack> secondaryOutput : recipe.secondaryOutputs)
                buffer.writeItem(secondaryOutput.get());
        }
    }
}
