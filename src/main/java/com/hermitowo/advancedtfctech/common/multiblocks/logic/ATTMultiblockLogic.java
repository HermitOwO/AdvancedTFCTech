package com.hermitowo.advancedtfctech.common.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistration;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.multiblocks.ATTMultiblocks;

public class ATTMultiblockLogic
{
    public static final MultiblockRegistration<ThresherLogic.State> THRESHER = metal(new ThresherLogic(), "thresher")
        .structure(() -> ATTMultiblocks.THRESHER)
        .redstone(s -> s.rsState, ThresherLogic.REDSTONE_POS)
        .gui(ATTContainerTypes.THRESHER)
        .build();

    public static final MultiblockRegistration<GristMillLogic.State> GRIST_MILL = metal(new GristMillLogic(), "grist_mill")
        .structure(() -> ATTMultiblocks.GRIST_MILL)
        .redstone(s -> s.rsState, GristMillLogic.REDSTONE_POS)
        .gui(ATTContainerTypes.GRIST_MILL)
        .build();

    public static final MultiblockRegistration<PowerLoomLogic.State> POWER_LOOM = metal(new PowerLoomLogic(), "power_loom")
        .structure(() -> ATTMultiblocks.POWER_LOOM)
        .redstone(s -> s.rsState, PowerLoomLogic.REDSTONE_POS)
        .build();

    public static final MultiblockRegistration<BeamhouseLogic.State> BEAMHOUSE = metal(new BeamhouseLogic(), "beamhouse")
        .structure(() -> ATTMultiblocks.BEAMHOUSE)
        .redstone(s -> s.rsState, BeamhouseLogic.REDSTONE_POS)
        .build();

    private static <S extends IMultiblockState> ATTMultiblockBuilder<S> metal(IMultiblockLogic<S> logic, String name)
    {
        return new ATTMultiblockBuilder<>(logic, name)
            .defaultBEs(ATTBlockEntities.BLOCK_ENTITIES)
            .defaultBlock(ATTBlocks.BLOCKS, ATTItems.ITEMS, IEBlocks.METAL_PROPERTIES_NO_OCCLUSION.get());
    }
}
