package com.hermitowo.advancedtfctech.common.recipes;

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

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class GristMillRecipe extends ATTMultiblockRecipe implements IItemStackProviderMultiblockRecipe
{
    public static final CachedRecipeList<GristMillRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.GRIST_MILL);

    public final IngredientWithSize input;
    public final ItemStackProvider output;

    public GristMillRecipe(ResourceLocation id, ItemStackProvider output, IngredientWithSize input, int time, int energy)
    {
        super(LAZY_EMPTY, ATTRecipeTypes.GRIST_MILL, id);
        this.output = output;
        this.input = input;

        timeAndEnergy(time, energy);

        setInputListWithSizes(Lists.newArrayList(this.input));
        this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.output.stack().get()));
        this.providerList = Lazy.of(() -> NonNullList.of(ItemStackProvider.empty(), this.output));
    }

    public static GristMillRecipe findRecipe(Level level, ItemStack stack)
    {
        for (GristMillRecipe recipe : RECIPES.getRecipes(level))
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
    protected IERecipeSerializer<GristMillRecipe> getIESerializer()
    {
        return ATTRecipeSerializers.GRIST_MILL_SERIALIZER.get();
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

    public static class Serializer extends IERecipeSerializer<GristMillRecipe>
    {
        @Override
        public ItemStack getIcon()
        {
            return ATTMultiblockLogic.GRIST_MILL.iconStack();
        }

        @Override
        public GristMillRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context)
        {
            ItemStackProvider output = ItemStackProvider.fromJson(GsonHelper.getAsJsonObject(json, "result"));
            IngredientWithSize input = IngredientWithSize.deserialize(GsonHelper.getAsJsonObject(json, "input"));
            int time = GsonHelper.getAsInt(json, "time");
            int energy = GsonHelper.getAsInt(json, "energy");

            return ATTConfig.SERVER.gristMillConfig.apply(new GristMillRecipe(recipeId, output, input, time, energy));
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
}