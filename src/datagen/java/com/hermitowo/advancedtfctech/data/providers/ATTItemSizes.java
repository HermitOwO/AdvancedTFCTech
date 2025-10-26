package com.hermitowo.advancedtfctech.data.providers;

import java.util.concurrent.CompletableFuture;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.data.Accessors;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.component.size.ItemSizeDefinition;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ATTItemSizes extends DataManagerProvider<ItemSizeDefinition> implements Accessors
{
    public ATTItemSizes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(ItemSizeManager.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("pirns", ingredientOf(
            Ingredient.of(ATTItems.PIRN),
            Ingredient.of(ATTItems.FIBER_WINDED_PIRN),
            Ingredient.of(ATTItems.SILK_WINDED_PIRN),
            Ingredient.of(ATTItems.WOOL_WINDED_PIRN),
            Ingredient.of(ATTItems.PINEAPPLE_WINDED_PIRN)
        ), Size.SMALL, Weight.LIGHT);
        add("fleshing_blades", ATTItems.FLESHING_BLADES, Size.LARGE, Weight.VERY_HEAVY);
        add("fleshing_machine", ATTBlocks.FLESHING_MACHINE, Size.VERY_LARGE, Weight.VERY_HEAVY);
    }

    private void add(String name, TagKey<Item> item, Size size, Weight weight)
    {
        add(name, new ItemSizeDefinition(Ingredient.of(item), size, weight));
    }

    private void add(String name, ItemLike item, Size size, Weight weight)
    {
        add(name, new ItemSizeDefinition(Ingredient.of(item), size, weight));
    }

    private void add(String name, Ingredient item, Size size, Weight weight)
    {
        add(name, new ItemSizeDefinition(item, size, weight));
    }
}
