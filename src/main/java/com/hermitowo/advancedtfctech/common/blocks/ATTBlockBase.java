package com.hermitowo.advancedtfctech.common.blocks;

import java.util.function.Supplier;
import com.hermitowo.advancedtfctech.common.ATTTabs;
import com.hermitowo.advancedtfctech.common.blocks.ticking.ATTClientTickableBlock;
import com.hermitowo.advancedtfctech.common.blocks.ticking.ATTCommonTickableBlock;
import com.hermitowo.advancedtfctech.common.blocks.ticking.ATTServerTickableBlock;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class ATTBlockBase extends Block
{
    public ATTBlockBase(Properties properties)
    {
        super(properties);
    }

    public Supplier<BlockItem> blockItemSupplier()
    {
        return () -> new ATTBlockItemBase(this, new Item.Properties().tab(ATTTabs.MAIN));
    }

    @Nullable
    public static <E extends BlockEntity & ATTCommonTickableBlock, A extends BlockEntity> BlockEntityTicker<A> createCommonTicker(boolean isClient, BlockEntityType<A> actual, RegistryObject<BlockEntityType<E>> expected)
    {
        return createCommonTicker(isClient, actual, expected.get());
    }

    @Nullable
    public static <E extends BlockEntity & ATTCommonTickableBlock, A extends BlockEntity> BlockEntityTicker<A> createCommonTicker(boolean isClient, BlockEntityType<A> actual, BlockEntityType<E> expected)
    {
        if(isClient)
        {
            return createClientTicker(actual, expected);
        }
        else
        {
            return createServerTicker(actual, expected);
        }
    }

    @Nullable
    public static <E extends BlockEntity & ATTClientTickableBlock, A extends BlockEntity> BlockEntityTicker<A> createClientTicker(BlockEntityType<A> actual, BlockEntityType<E> expected)
    {
        return createTickerHelper(actual, expected, ATTClientTickableBlock::makeTicker);
    }

    @Nullable
    public static <E extends BlockEntity & ATTServerTickableBlock, A extends BlockEntity> BlockEntityTicker<A> createServerTicker(BlockEntityType<A> actual, BlockEntityType<E> expected)
    {
        return createTickerHelper(actual, expected, ATTServerTickableBlock::makeTicker);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> actual, BlockEntityType<E> expected, Supplier<BlockEntityTicker<? super E>> ticker)
    {
        return expected == actual ? (BlockEntityTicker<A>) ticker.get() : null;
    }
}

