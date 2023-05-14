package com.hermitowo.advancedtfctech.api.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.common.collect.Lists;
import com.hermitowo.advancedtfctech.api.crafting.cache.CachedRecipeList;
import com.hermitowo.advancedtfctech.common.crafting.ATTSerializers;
import com.hermitowo.advancedtfctech.config.ATTServerConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.Lazy;

public class GristMillRecipe extends ATTMultiblockRecipe
{
    public static final CachedRecipeList<GristMillRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.GRIST_MILL);

    public IngredientWithSize input;
    public final Lazy<ItemStack> output;

    public GristMillRecipe(ResourceLocation id, Lazy<ItemStack> output, IngredientWithSize input, int time, int energy)
    {
        super(ItemStack.EMPTY, ATTRecipeTypes.GRIST_MILL, id);
        this.output = output;
        this.input = input;

        timeAndEnergy(time, energy);
        modifyTimeAndEnergy(ATTServerConfig.GENERAL.gristMill_timeModifier::get, ATTServerConfig.GENERAL.gristMill_energyModifier::get);

        setInputListWithSizes(Lists.newArrayList(this.input));
        this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.output.get()));
    }

    public static GristMillRecipe findRecipe(Level level, ItemStack stack)
    {
        for(GristMillRecipe recipe:RECIPES.getRecipes(level))
        {
            if(recipe.input != null && recipe.input.test(stack))
            {
                return recipe;
            }
        }
        return null;
    }

    public boolean isValidInput(ItemStack stack)
    {
        return this.input != null && this.input.test(stack);

    }

    public static boolean isValidRecipeInput(Level level, ItemStack stack)
    {
        for (GristMillRecipe recipe : RECIPES.getRecipes(level))
            if (recipe != null && recipe.isValidInput(stack))
                return true;
        return false;
    }

    @Override
    public int getMultipleProcessTicks()
    {
        return 0;
    }

    @Override
    public NonNullList<ItemStack> getActualItemOutputs(BlockEntity blockentity)
    {
        NonNullList<ItemStack> list = NonNullList.create();
        list.add(output.get());
        return list;
    }

    @Override
    protected IERecipeSerializer<GristMillRecipe> getIESerializer()
    {
        return ATTSerializers.GRIST_MILL_SERIALIZER.get();
    }
}