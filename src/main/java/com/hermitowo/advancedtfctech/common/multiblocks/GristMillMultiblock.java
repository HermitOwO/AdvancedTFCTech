package com.hermitowo.advancedtfctech.common.multiblocks;

import java.util.function.Consumer;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.client.ATTClientMultiblockProperties;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import net.minecraft.core.BlockPos;

public class GristMillMultiblock extends IETemplateMultiblock
{
    public GristMillMultiblock()
    {
        super(AdvancedTFCTech.rl("multiblocks/grist_mill"),
            new BlockPos(1, 0, 1), new BlockPos(2, 1, 1), new BlockPos(4, 3, 3),
            ATTMultiblockLogic.GRIST_MILL);
    }

    @Override
    public float getManualScale()
    {
        return 13;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer)
    {
        consumer.accept(new ATTClientMultiblockProperties(this));
    }
}
