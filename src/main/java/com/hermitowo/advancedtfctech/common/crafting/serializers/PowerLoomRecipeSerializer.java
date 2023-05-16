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
        String category = GsonHelper.getAsString(json, "category");
        Lazy<ItemStack> output = readOutput(json.get("result"));
        IngredientWithSize[] ingredients;
        if(json.has("input"))
            ingredients = new IngredientWithSize[]{
                IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "input"))
            };
        else
        {
            JsonArray inputs = json.getAsJsonArray("inputs");
            ingredients = new IngredientWithSize[inputs.size()];
            for(int i = 0; i < ingredients.length; i++)
                ingredients[i] = IngredientWithSize.deserialize(inputs.get(i));
        }
        int time = GsonHelper.getAsInt(json, "time");
        int energy = GsonHelper.getAsInt(json, "energy");

        return new PowerLoomRecipe(recipeId, output, ingredients, category, time, energy);
    }

    @Nullable
    @Override
    public PowerLoomRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
    {
        String category = buffer.readUtf();
        Lazy<ItemStack> output = readLazyStack(buffer);
        int inputCount = buffer.readInt();
        IngredientWithSize[] ingredients = new IngredientWithSize[inputCount];
        for(int i = 0; i < ingredients.length; i++)
            ingredients[i] = IngredientWithSize.read(buffer);
        int time = buffer.readInt();
        int energy = buffer.readInt();

        return new PowerLoomRecipe(recipeId, output, ingredients, category, time, energy);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, PowerLoomRecipe recipe)
    {
        buffer.writeUtf(recipe.category);
        writeLazyStack(buffer, recipe.output);
        buffer.writeInt(recipe.inputs.length);
        for(IngredientWithSize ingredient : recipe.inputs)
            ingredient.write(buffer);
    }
}
