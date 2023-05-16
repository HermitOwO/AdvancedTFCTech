package com.hermitowo.advancedtfctech.common.blocks.ticking;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface ATTServerTickableBlock
{
    void tickServer();

    static <T extends BlockEntity & ATTServerTickableBlock> BlockEntityTicker<T> makeTicker()
    {
        return (level, pos, state, blockentity) -> blockentity.tickServer();
    }
}
