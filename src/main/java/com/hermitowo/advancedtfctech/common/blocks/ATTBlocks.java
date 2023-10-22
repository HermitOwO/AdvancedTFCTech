package com.hermitowo.advancedtfctech.common.blocks;

import java.util.function.Function;
import java.util.function.Supplier;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import com.hermitowo.advancedtfctech.common.ATTTabs;
import com.hermitowo.advancedtfctech.common.blocks.metal.FleshingMachineBlock;
import com.hermitowo.advancedtfctech.common.blocks.multiblocks.BeamhouseBlock;
import com.hermitowo.advancedtfctech.common.blocks.multiblocks.GristMillBlock;
import com.hermitowo.advancedtfctech.common.blocks.multiblocks.PowerLoomBlock;
import com.hermitowo.advancedtfctech.common.blocks.multiblocks.ThresherBlock;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockConstructor, @Nullable Function<T, ? extends BlockItem> blockItem)
    {
        RegistryObject<T> block = BLOCKS.register(name, blockConstructor);
        if (blockItem != null)
        {
            ATTItems.register(name, () -> blockItem.apply(block.get()));
        }
        return block;
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockConstructor)
    {
        return register(name, blockConstructor, block -> new BlockItemIE(block, new Item.Properties().tab(ATTTabs.MAIN)));
    }

    public static <T extends Block> RegistryObject<T> registerMultiblockBlock(String name, Supplier<T> blockConstructor)
    {
        return register(name, blockConstructor, block -> new BlockItem(block, new Item.Properties()));
    }

    public static class Blocks
    {
        public static final RegistryObject<FleshingMachineBlock> FLESHING_MACHINE = registerBlock("fleshing_machine", FleshingMachineBlock::new);
    }

    public static class Multiblocks
    {
        public static final RegistryObject<ThresherBlock> THRESHER = registerMultiblockBlock("thresher", ThresherBlock::new);
        public static final RegistryObject<GristMillBlock> GRIST_MILL = registerMultiblockBlock("grist_mill", GristMillBlock::new);
        public static final RegistryObject<PowerLoomBlock> POWER_LOOM = registerMultiblockBlock("power_loom", PowerLoomBlock::new);
        public static final RegistryObject<BeamhouseBlock> BEAMHOUSE = registerMultiblockBlock("beamhouse", BeamhouseBlock::new);
    }
}
