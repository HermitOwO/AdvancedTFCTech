package com.hermitowo.advancedtfctech.common.blocks.ticking;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface ATTClientTickableBlock
{
    void tickClient();

    static <T extends BlockEntity & ATTClientTickableBlock> BlockEntityTicker<T> makeTicker()
    {
        return (level, pos, state, blockentity) -> blockentity.tickClient();
    }
}
