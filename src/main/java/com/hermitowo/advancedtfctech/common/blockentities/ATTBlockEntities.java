package com.hermitowo.advancedtfctech.common.blockentities;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ATTBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AdvancedTFCTech.MOD_ID);

    public static final MultiblockBEType<FleshingMachineBlockEntity> FLESHING_MACHINE = registerMultiblockBE("fleshing_machine", FleshingMachineBlockEntity::new, ATTBlocks.FLESHING_MACHINE);

    public static <T extends BlockEntity & IEBlockInterfaces.IGeneralMultiblock> MultiblockBEType<T> registerMultiblockBE(String name, MultiblockBEType.BEWithTypeConstructor<T> make, Supplier<? extends Block> block)
    {
        return new MultiblockBEType<>(name, BLOCK_ENTITIES, make, block, state -> state.hasProperty(IEProperties.MULTIBLOCKSLAVE) && !state.getValue(IEProperties.MULTIBLOCKSLAVE));
    }
}