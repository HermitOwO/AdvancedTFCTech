package com.hermitowo.advancedtfctech.common.multiblocks;

import java.util.function.Consumer;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class PowerLoomMultiblock extends ATTTemplateMultiblock
{
    public static final PowerLoomMultiblock INSTANCE = new PowerLoomMultiblock();

    public PowerLoomMultiblock()
    {
        super(new ResourceLocation(MOD_ID, "multiblocks/power_loom"),
            new BlockPos(1, 0, 2), new BlockPos(1, 1, 4), new BlockPos(3, 3, 5), ATTBlocks.Multiblocks.POWER_LOOM);
    }

    @Override
    public float getManualScale()
    {
        return 13;
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer)
    {
        consumer.accept(new ATTClientMultiblockProperties(this, 1.25, 0.75, 2.75));
    }
}
