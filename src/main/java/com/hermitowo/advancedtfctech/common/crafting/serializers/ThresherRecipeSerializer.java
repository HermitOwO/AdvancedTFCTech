package com.hermitowo.advancedtfctech.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hermitowo.advancedtfctech.api.crafting.ThresherRecipe;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class ThresherRecipeSerializer extends IERecipeSerializer<ThresherRecipe>
{
    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(ATTBlocks.Multiblocks.THRESHER.get());
    }

    @Override
    public ThresherRecipe readFromJson(ResourceLocation recipeId, JsonObject json, IContext context)
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

        ThresherRecipe recipe = new ThresherRecipe(recipeId, output, input, time, energy);

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