package com.hermitowo.advancedtfctech.common.items;

import java.util.Locale;
import java.util.function.Supplier;
import com.hermitowo.advancedtfctech.common.ATTTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

@SuppressWarnings("unused")
public class ATTItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> PIRN = register("pirn", (ATTTabs.MAIN));
    public static final RegistryObject<Item> FIBER_WINDED_PIRN  = register("fiber_winded_pirn", (ATTTabs.MAIN));
    public static final RegistryObject<Item> SILK_WINDED_PIRN  = register("silk_winded_pirn", (ATTTabs.MAIN));
    public static final RegistryObject<Item> WOOL_WINDED_PIRN  = register("wool_winded_pirn", (ATTTabs.MAIN));

    private static RegistryObject<Item> register(String name, CreativeModeTab group)
    {
        return register(name, () -> new Item(new Item.Properties().tab(group)));
    }

    public static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item)
    {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), item);
    }
}