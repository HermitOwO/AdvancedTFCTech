package com.hermitowo.advancedtfctech.common.multiblocks;

import java.util.function.Consumer;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class GristMillMultiblock extends ATTTemplateMultiblock
{
    public static final GristMillMultiblock INSTANCE = new GristMillMultiblock();

    public GristMillMultiblock()
    {
        super(new ResourceLocation(MOD_ID, "multiblocks/grist_mill"),
            new BlockPos(1, 0, 1), new BlockPos(2, 1, 1), new BlockPos(4, 3, 3), ATTBlocks.Multiblocks.GRIST_MILL);
    }

    @Override
    public float getManualScale()
    {
        return 13;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer)
    {
        consumer.accept(new ATTClientMultiblockProperties(this, 1.0, 0.75, 2.0));
    }

}
