package com.hermitowo.advancedtfctech.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hermitowo.advancedtfctech.api.crafting.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

public class PowerLoomRecipeSerializer extends IERecipeSerializer<PowerLoomRecipe>
{
    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(ATTBlocks.Multiblocks.POWER_LOOM.get());
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

        PowerLoomRecipe recipe = new PowerLoomRecipe(recipeId, output, ingredients, secondaryInput, inProgressTexture, time, energy);

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
