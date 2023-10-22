package com.hermitowo.advancedtfctech.common.recipes;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IESerializableRecipe;
import com.google.gson.JsonObject;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.recipes.cache.CachedRecipeList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class FleshingMachineRecipe extends IESerializableRecipe
{
    public static final CachedRecipeList<FleshingMachineRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.FLESHING_MACHINE);

    public final ItemStackProvider output;
    public final Ingredient input;
    public final int time;
    public final int energy;

    public FleshingMachineRecipe(ResourceLocation id, ItemStackProvider output, Ingredient input, int time, int energy)
    {
        super(Lazy.of(() -> ItemStack.EMPTY), ATTRecipeTypes.FLESHING_MACHINE.get(), id);
        this.output = output;
        this.input = input;
        this.time = time;
        this.energy = energy;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem()
    {
        return this.output.getEmptyStack();
    }

    public static FleshingMachineRecipe findRecipe(Level level, ItemStack stack, @Nullable FleshingMachineRecipe hint)
    {
        if (stack.isEmpty())
            return null;
        if (hint != null && hint.isValidInput(stack))
            return hint;
        for (FleshingMachineRecipe recipe : RECIPES.getRecipes(level))
            if (recipe.input != null && recipe.input.test(stack))
                return recipe;
        return null;
    }

    public static FleshingMachineRecipe findRecipe(Level level, ItemStack stack)
    {
        return findRecipe(level, stack, null);
    }

    public boolean isValidInput(ItemStack stack)
    {
        return this.input != null && this.input.test(stack);
    }

    public static boolean isValidRecipeInput(Level level, ItemStack stack)
    {
        for (FleshingMachineRecipe recipe : RECIPES.getRecipes(level))
            if (recipe != null && recipe.isValidInput(stack))
                return true;
        return false;
    }

    public int getTime()
    {
        return this.time;
    }

    public int getEnergy()
    {
        return this.energy;
    }

    @Override
    protected IERecipeSerializer<?> getIESerializer()
    {
        return ATTRecipeSerializers.FLESHING_MACHINE_SERIALIZER.get();
    }

    public static class Serializer extends IERecipeSerializer<FleshingMachineRecipe>
    {
        @Override
        public ItemStack getIcon()
        {
            return new ItemStack(ATTBlocks.Blocks.FLESHING_MACHINE.get());
        }

        @Override
        public FleshingMachineRecipe readFromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context)
        {
            ItemStackProvider output = ItemStackProvider.fromJson(GsonHelper.getAsJsonObject(json, "result"));
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            int time = GsonHelper.getAsInt(json, "time");
            int energy = GsonHelper.getAsInt(json, "energy");

            return new FleshingMachineRecipe(recipeId, output, input, time, energy);
        }

        @Nullable
        @Override
        public FleshingMachineRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            ItemStackProvider output = ItemStackProvider.fromNetwork(buffer);
            Ingredient input = Ingredient.fromNetwork(buffer);
            int time = buffer.readInt();
            int energy = buffer.readInt();

            return new FleshingMachineRecipe(recipeId, output, input, time, energy);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FleshingMachineRecipe recipe)
        {
            recipe.output.toNetwork(buffer);
            recipe.input.toNetwork(buffer);
            buffer.writeInt(recipe.time);
            buffer.writeInt(recipe.energy);
        }
    }
}
