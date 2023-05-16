package com.hermitowo.advancedtfctech.api.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hermitowo.advancedtfctech.api.crafting.cache.CachedRecipeList;
import com.hermitowo.advancedtfctech.common.crafting.ATTSerializers;
import com.hermitowo.advancedtfctech.config.ATTServerConfig;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.ItemHandlerHelper;

public class PowerLoomRecipe extends ATTMultiblockRecipe
{
    public static final CachedRecipeList<PowerLoomRecipe> RECIPES = new CachedRecipeList<>(ATTRecipeTypes.POWER_LOOM);
    private static int reloadCountForCategories = CachedRecipeList.INVALID_RELOAD_COUNT;
    private static Map<String, List<PowerLoomRecipe>> recipesByCategory = Collections.emptyMap();

    public final IngredientWithSize[] inputs;
    public final Lazy<ItemStack> output;
    public final String category;

    public PowerLoomRecipe(ResourceLocation id, Lazy<ItemStack> output, IngredientWithSize[] inputs, String category, int time, int energy)
    {
        super(ItemStack.EMPTY, ATTRecipeTypes.POWER_LOOM, id);
        this.output = output;
        this.inputs = inputs;
        this.category = category;

        timeAndEnergy(time, energy);
        modifyTimeAndEnergy(ATTServerConfig.GENERAL.powerLoom_timeModifier::get, ATTServerConfig.GENERAL.powerLoom_energyModifier::get);

        setInputListWithSizes(Lists.newArrayList(this.inputs));
        this.outputList = Lazy.of(() -> NonNullList.of(ItemStack.EMPTY, this.output.get()));
    }

    public int getMaxCrafted(NonNullList<ItemStack> query)
    {
        HashMap<ItemStack, Integer> queryAmount = new HashMap<>();
        for(ItemStack q : query)
            if(!q.isEmpty())
            {
                boolean inc = false;
                for(ItemStack key : queryAmount.keySet())
                    if(ItemHandlerHelper.canItemStacksStack(q, key))
                    {
                        queryAmount.put(key, queryAmount.get(key)+q.getCount());
                        inc = true;
                    }
                if(!inc)
                    queryAmount.put(q, q.getCount());
            }

        OptionalInt maxCrafted = OptionalInt.empty();
        for(IngredientWithSize ingr : inputs)
        {
            int maxCraftedWithIngredient = 0;
            int req = ingr.getCount();
            Iterator<Map.Entry<ItemStack, Integer>> queryIt = queryAmount.entrySet().iterator();
            while(queryIt.hasNext())
            {
                Map.Entry<ItemStack, Integer> e = queryIt.next();
                ItemStack compStack = e.getKey();
                if(ingr.test(compStack))
                {
                    int taken = e.getValue()/req;
                    if(taken > 0)
                    {
                        e.setValue(e.getValue()-taken*req);
                        if(e.getValue() <= 0)
                            queryIt.remove();
                        maxCraftedWithIngredient += taken;
                    }
                }
            }
            if(maxCraftedWithIngredient <= 0)
                return 0;
            else if(maxCrafted.isPresent())
                maxCrafted = OptionalInt.of(Math.min(maxCrafted.getAsInt(), maxCraftedWithIngredient));
            else
                maxCrafted = OptionalInt.of(maxCraftedWithIngredient);
        }
        return maxCrafted.orElse(0);
    }

    public NonNullList<ItemStack> consumeInputs(NonNullList<ItemStack> query, int crafted)
    {
        List<IngredientWithSize> inputList = new ArrayList<>(inputs.length);
        for(IngredientWithSize i : inputs)
            if(i != null)
                inputList.add(i);

        NonNullList<ItemStack> consumed = NonNullList.create();
        Iterator<IngredientWithSize> inputIt = inputList.iterator();
        while(inputIt.hasNext())
        {
            IngredientWithSize ingr = inputIt.next();
            int inputSize = ingr.getCount()*crafted;

            for(int i = 0; i < query.size(); i++)
            {
                ItemStack queryStack = query.get(i);
                if(!queryStack.isEmpty())
                    if(ingr.test(queryStack))
                    {
                        int taken = Math.min(queryStack.getCount(), inputSize);
                        consumed.add(ItemHandlerHelper.copyStackWithSize(queryStack, taken));
                        if(taken >= queryStack.getCount() && queryStack.getItem().hasContainerItem(queryStack))
                            query.set(i, queryStack.getItem().getContainerItem(queryStack));
                        else
                            queryStack.shrink(taken);
                        inputSize -= taken;
                        if(inputSize <= 0)
                        {
                            inputIt.remove();
                            break;
                        }
                    }

            }
        }
        return consumed;
    }

    public static PowerLoomRecipe[] findRecipes(Level level, String category)
    {
        updateRecipeCategories(level);
        return recipesByCategory.getOrDefault(category, ImmutableList.of()).toArray(new PowerLoomRecipe[0]);
    }

    public static void updateRecipeCategories(Level level)
    {
        if (reloadCountForCategories == CachedRecipeList.getReloadCount())
            return;
        recipesByCategory = RECIPES.getRecipes(level).stream()
            .collect(Collectors.groupingBy(r -> r.category));
        reloadCountForCategories = CachedRecipeList.getReloadCount();
    }

    @Override
    public int getMultipleProcessTicks()
    {
        return 0;
    }

    @Override
    protected IERecipeSerializer<PowerLoomRecipe> getIESerializer()
    {
        return ATTSerializers.POWER_LOOM_SERIALIZER.get();
    }
}
