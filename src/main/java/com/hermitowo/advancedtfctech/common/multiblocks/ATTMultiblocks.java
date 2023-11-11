package com.hermitowo.advancedtfctech.common.multiblocks;

import java.util.ArrayList;
import java.util.List;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;

public class ATTMultiblocks
{
    public static final List<MultiblockHandler.IMultiblock> ATT_MULTIBLOCKS = new ArrayList<>();

    public static IETemplateMultiblock THRESHER;
    public static IETemplateMultiblock GRIST_MILL;
    public static IETemplateMultiblock POWER_LOOM;
    public static IETemplateMultiblock BEAMHOUSE;

    public static void init()
    {
        THRESHER = register(new ThresherMultiblock());
        GRIST_MILL = register(new GristMillMultiblock());
        POWER_LOOM = register(new PowerLoomMultiblock());
        BEAMHOUSE = register(new BeamhouseMultiblock());
    }

    private static <T extends MultiblockHandler.IMultiblock> T register(T multiblock)
    {
        ATT_MULTIBLOCKS.add(multiblock);
        MultiblockHandler.registerMultiblock(multiblock);
        return multiblock;
    }
}
