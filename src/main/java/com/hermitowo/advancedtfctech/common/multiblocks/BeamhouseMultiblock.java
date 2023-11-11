package com.hermitowo.advancedtfctech.common.multiblocks;

import java.util.function.Consumer;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.client.ATTClientMultiblockProperties;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import net.minecraft.core.BlockPos;

public class BeamhouseMultiblock extends IETemplateMultiblock
{
    public BeamhouseMultiblock()
    {
        super(AdvancedTFCTech.rl("multiblocks/beamhouse"),
            new BlockPos(2, 1, 1), new BlockPos(1, 1, 2), new BlockPos(4, 3, 4),
            ATTMultiblockLogic.BEAMHOUSE);
    }

    @Override
    public float getManualScale()
    {
        return 14;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer)
    {
        consumer.accept(new ATTClientMultiblockProperties(this));
    }
}
