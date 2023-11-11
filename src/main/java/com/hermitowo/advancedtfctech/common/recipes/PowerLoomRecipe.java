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

public class PowerLoomRecipe extends ATTMultiblockRecipe
{
    public static final CachedRecipeList<PowerLoomRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.POWER_LOOM);

    public final IngredientWithSize[] inputs;
    public final IngredientWithSize secondaryInput;
    public final Lazy<ItemStack> output;
    public final NonNullList<Lazy<ItemStack>> secondaryOutputs = NonNullList.create();
    public final ResourceLocation inProgressTexture;

    public PowerLoomRecipe(ResourceLocation id, Lazy<ItemStack> output, IngredientWithSize[] inputs, IngredientWithSize secondaryInput, ResourceLocation inProgressTexture, int time, int energy)
    {
        super(LAZY_EMPTY, ATTRecipeTypes.POWER_LOOM, id);
        this.output = output;
        this.inputs = inputs;
        this.secondaryInput = secondaryInput;
        this.inProgressTexture = inProgressTexture;

        timeAndEnergy(time, energy);

        setInputListWithSizes(Lists.newArrayList(this.inputs));
        this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.output.get()));
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

    public static PowerLoomRecipe findRecipe(Level level, ItemStack pirn, ItemStack weave)
    {
        if (pirn.isEmpty() || weave.isEmpty())
            return null;
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe.matches(pirn, weave))
                return recipe;
        return null;
    }

    public static PowerLoomRecipe findRecipeForRendering(Level level, ItemStack secondaryInput)
    {
        if (secondaryInput.isEmpty())
            return null;
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe.isValidSecondaryInput(secondaryInput))
                return recipe;
        return null;
    }

    public boolean matches(ItemStack pirn, ItemStack weave)
    {
        return isValidPirn(pirn) && isValidWeaveWithSize(weave);
    }

    public boolean isValidPirn(ItemStack stack)
    {
        return this.inputs[1] != null && this.inputs[1].test(stack);
    }

    public static boolean isValidPirnInput(Level level, ItemStack stack)
    {
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe != null && recipe.isValidPirn(stack))
                return true;
        return false;
    }

    public boolean isValidWeave(ItemStack stack)
    {
        return this.inputs[0] != null && this.inputs[0].testIgnoringSize(stack);
    }

    public boolean isValidWeaveWithSize(ItemStack stack)
    {
        return this.inputs[0] != null && this.inputs[0].test(stack);
    }

    public static boolean isValidWeaveInput(Level level, ItemStack stack)
    {
        for (PowerLoomRecipe recipe : RECIPES.getRecipes(level))
            if (recipe != null && recipe.isValidWeave(stack))
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
        @Override
        public ItemStack getIcon()
        {
            return ATTMultiblockLogic.POWER_LOOM.iconStack();
        }

        @Override
        public PowerLoomRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context)
        {
            Lazy<ItemStack> output = readOutput(json.get("result"));
            IngredientWithSize[] ingredients;
            if (json.has("input"))
                ingredients = new IngredientWithSize[] {
                    IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "input"))
                };
            else
            {
                JsonArray inputs = json.getAsJsonArray("inputs");
                ingredients = new IngredientWithSize[inputs.size()];
                for (int i = 0; i < ingredients.length; i++)
                    ingredients[i] = IngredientWithSize.deserialize(inputs.get(i));
            }
            IngredientWithSize secondaryInput = IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "secondary_input"));
            ResourceLocation inProgressTexture = new ResourceLocation(GsonHelper.getAsString(json, "in_progress_texture"));
            int time = GsonHelper.getAsInt(json, "time");
            int energy = GsonHelper.getAsInt(json, "energy");

            PowerLoomRecipe recipe = ATTConfig.SERVER.powerLoomConfig.apply(new PowerLoomRecipe(recipeId, output, ingredients, secondaryInput, inProgressTexture, time, energy));

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
        public PowerLoomRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            Lazy<ItemStack> output = readLazyStack(buffer);
            int inputCount = buffer.readInt();
            IngredientWithSize[] ingredients = new IngredientWithSize[inputCount];
            for (int i = 0; i < ingredients.length; i++)
                ingredients[i] = IngredientWithSize.read(buffer);
            IngredientWithSize secondaryInput = IngredientWithSize.read(buffer);
            ResourceLocation inProgressTexture = new ResourceLocation(buffer.readUtf());
            int time = buffer.readInt();
            int energy = buffer.readInt();

            PowerLoomRecipe recipe = new PowerLoomRecipe(recipeId, output, ingredients, secondaryInput, inProgressTexture, time, energy);

            int secondaryCount = buffer.readInt();
            for (int i = 0; i < secondaryCount; i++)
                recipe.addToSecondaryOutput(readLazyStack(buffer));

            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PowerLoomRecipe recipe)
        {
            writeLazyStack(buffer, recipe.output);
            buffer.writeInt(recipe.inputs.length);
            for (IngredientWithSize ingredient : recipe.inputs)
                ingredient.write(buffer);
            recipe.secondaryInput.write(buffer);
            buffer.writeUtf(recipe.inProgressTexture.toString());
            buffer.writeInt(recipe.getTotalProcessTime());
            buffer.writeInt(recipe.getTotalProcessEnergy());

            buffer.writeInt(recipe.secondaryOutputs.size());
            for (Lazy<ItemStack> secondaryOutput : recipe.secondaryOutputs)
                buffer.writeItem(secondaryOutput.get());
        }
    }
}
