package com.hermitowo.advancedtfctech.common;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockPartBlock;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ATTCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdvancedTFCTech.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = register("main", () -> new ItemStack(ATTItems.PIRN.get()), ATTCreativeTabs::fillTab);

    private static RegistryObject<CreativeModeTab> register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        return CREATIVE_TABS.register(name, () -> CreativeModeTab.builder().icon(icon).title(Component.translatable("advancedtfctech.creative_tab." + name)).displayItems(displayItems).build());
    }

    private static void fillTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        for (final RegistryObject<Item> itemRef : ATTItems.ITEMS.getEntries())
        {
            final Item item = itemRef.get();
            if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof MultiblockPartBlock<?>)
                return;
            else if (item == ATTItems.PINEAPPLE_WINDED_PIRN.get())
            {
                if (ModList.get().isLoaded("firmalife"))
                {
                    out.accept(item);
                }
            }
            else out.accept(item);
        }
    }
}
