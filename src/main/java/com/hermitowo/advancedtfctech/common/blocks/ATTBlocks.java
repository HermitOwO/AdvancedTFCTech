package com.hermitowo.advancedtfctech.common.blocks;

import java.util.function.Function;
import java.util.function.Supplier;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATTBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AdvancedTFCTech.MOD_ID);

    public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockConstructor, @Nullable Function<T, ? extends BlockItem> blockItem)
    {
        RegistryObject<T> block = BLOCKS.register(name, blockConstructor);
        if (blockItem != null)
            ATTItems.register(name, () -> blockItem.apply(block.get()));
        return block;
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockConstructor)
    {
        return register(name, blockConstructor, block -> new BlockItemIE(block, new Item.Properties()));
    }

    public static final RegistryObject<FleshingMachineBlock> FLESHING_MACHINE = registerBlock("fleshing_machine", FleshingMachineBlock::new);
}
