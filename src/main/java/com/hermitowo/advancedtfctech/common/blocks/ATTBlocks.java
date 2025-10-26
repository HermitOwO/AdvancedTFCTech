package com.hermitowo.advancedtfctech.common.blocks;

import java.util.function.Function;
import java.util.function.Supplier;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.blocks.TFCBlocks.Id;
import net.dries007.tfc.util.registry.RegistrationHelpers;


public class ATTBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, AdvancedTFCTech.MOD_ID);

    public static <T extends Block> Id<T> register(String name, Supplier<T> blockConstructor, @Nullable Function<T, ? extends BlockItem> blockItem)
    {
        return new Id<>(RegistrationHelpers.registerBlock(ATTBlocks.BLOCKS, ATTItems.ITEMS, name, blockConstructor, blockItem));
    }

    public static <T extends Block> Id<T> registerBlock(String name, Supplier<T> blockConstructor)
    {
        return register(name, blockConstructor, block -> new BlockItemIE(block, new Item.Properties()));
    }

    public static final Id<FleshingMachineBlock> FLESHING_MACHINE = registerBlock("fleshing_machine", FleshingMachineBlock::new);
}
