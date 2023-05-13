package com.hermitowo.advancedtfctech.common.blocks;

import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import com.hermitowo.advancedtfctech.common.blocks.ticking.ATTCommonTickableBlock;
import javax.annotation.Nonnull;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class ATTMetalMultiblock<T extends MultiblockPartBlockEntity<T> & ATTCommonTickableBlock> extends MetalMultiblockBlock<T>
{
    private final MultiblockBEType<T> multiblockBEType;

    public ATTMetalMultiblock(MultiblockBEType<T> blockentity)
    {
        super(blockentity, Block.Properties.of(Material.METAL)
            .sound(SoundType.METAL)
            .strength(3, 15)
            .requiresCorrectToolForDrops()
            .isViewBlocking((state, blockReader, pos) -> false)
            .noOcclusion()
        );
        this.multiblockBEType = blockentity;
    }

    @Override
    public <E extends BlockEntity> BlockEntityTicker<E> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<E> type)
    {
        return ATTBlockBase.createCommonTicker(world.isClientSide, type, multiblockBEType.master());
    }
}