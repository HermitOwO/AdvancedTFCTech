package com.hermitowo.advancedtfctech.common.multiblocks;

import java.util.function.Consumer;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.client.ATTClientMultiblockProperties;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import net.minecraft.core.BlockPos;

public class PowerLoomMultiblock extends IETemplateMultiblock
{
    public PowerLoomMultiblock()
    {
        super(AdvancedTFCTech.rl("multiblocks/power_loom"),
            new BlockPos(1, 0, 2), new BlockPos(1, 1, 4), new BlockPos(3, 3, 5),
            ATTMultiblockLogic.POWER_LOOM);
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
