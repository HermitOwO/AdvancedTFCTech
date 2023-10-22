package com.hermitowo.advancedtfctech.common.multiblocks;

import java.util.function.Consumer;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class BeamhouseMultiblock extends ATTTemplateMultiblock
{
    public static final BeamhouseMultiblock INSTANCE = new BeamhouseMultiblock();

    public BeamhouseMultiblock()
    {
        super(new ResourceLocation(MOD_ID, "multiblocks/beamhouse"),
            new BlockPos(1, 0, 1), new BlockPos(1, 1, 2), new BlockPos(4, 3, 4), ATTBlocks.Multiblocks.BEAMHOUSE);
    }

    @Override
    public float getManualScale()
    {
        return 14;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer)
    {
        consumer.accept(new ATTClientMultiblockProperties(this, 1.5, 0.5, 1.25));
    }
}
