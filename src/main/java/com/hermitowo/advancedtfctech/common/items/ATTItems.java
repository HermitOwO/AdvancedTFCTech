package com.hermitowo.advancedtfctech.common.items;

import java.util.Locale;
import java.util.function.Supplier;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.items.TFCItems.ItemId;

public class ATTItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, AdvancedTFCTech.MOD_ID);

    public static final ItemId PIRN = register("pirn");
    public static final ItemId FIBER_WINDED_PIRN = register("fiber_winded_pirn");
    public static final ItemId SILK_WINDED_PIRN = register("silk_winded_pirn");
    public static final ItemId WOOL_WINDED_PIRN = register("wool_winded_pirn");
    public static final ItemId PINEAPPLE_WINDED_PIRN = register("pineapple_winded_pirn");

    public static final ItemId FLESHING_BLADES = register("fleshing_blades", FleshingBladesItem::new);

    private static ItemId register(String name)
    {
        return register(name, () -> new Item(new Item.Properties()));
    }

    private static ItemId register(String name, Supplier<Item> item)
    {
        return new ItemId(ITEMS.register(name.toLowerCase(Locale.ROOT), item));
    }
}