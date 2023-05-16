package com.hermitowo.advancedtfctech.common.multiblocks;

import java.util.function.Consumer;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ThresherMultiblock extends ATTTemplateMultiblock
{
    public static final ThresherMultiblock INSTANCE = new ThresherMultiblock();

    public ThresherMultiblock()
    {
        super(new ResourceLocation(MOD_ID, "multiblocks/thresher"),
            new BlockPos(1, 0, 1), new BlockPos(1, 1, 2), new BlockPos(3, 3, 3), ATTBlocks.Multiblocks.THRESHER);
    }

    @Override
    public float getManualScale()
    {
        return 14;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer)
    {
        consumer.accept(new ATTClientMultiblockProperties(this, 1.0, 0.75, 2.0));
    }
}
