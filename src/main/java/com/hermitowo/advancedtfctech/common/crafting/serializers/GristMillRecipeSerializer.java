package com.hermitowo.advancedtfctech.common.crafting.serializers;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import com.hermitowo.advancedtfctech.api.crafting.GristMillRecipe;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class GristMillRecipeSerializer extends IERecipeSerializer<GristMillRecipe>
{
    @Override
    public ItemStack getIcon()
    {
        return new ItemStack(ATTBlocks.Multiblocks.GRIST_MILL.get());
    }

    @Override
    public GristMillRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context)
    {
        ItemStackProvider output = ItemStackProvider.fromJson(GsonHelper.getAsJsonObject(json, "result"));
        IngredientWithSize input = IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "input"));
        int time = GsonHelper.getAsInt(json, "time");
        int energy = GsonHelper.getAsInt(json, "energy");

        return new GristMillRecipe(recipeId, output, input, time, energy);
    }

    @Nullable
    @Override
    public GristMillRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
    {
        ItemStackProvider output = ItemStackProvider.fromNetwork(buffer);
        IngredientWithSize input = IngredientWithSize.read(buffer);
        int time = buffer.readInt();
        int energy = buffer.readInt();

        return new GristMillRecipe(recipeId, output, input, time, energy);
    }

    public void toNetwork(FriendlyByteBuf buffer, GristMillRecipe recipe)
    {
        recipe.output.toNetwork(buffer);
        recipe.input.write(buffer);
        buffer.writeInt(recipe.getTotalProcessTime());
        buffer.writeInt(recipe.getTotalProcessEnergy());
    }
}
