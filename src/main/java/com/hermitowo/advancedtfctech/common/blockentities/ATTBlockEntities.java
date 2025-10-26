package com.hermitowo.advancedtfctech.common.blockentities;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ATTBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AdvancedTFCTech.MOD_ID);

    public static final MultiblockBEType<FleshingMachineBlockEntity> FLESHING_MACHINE = registerMultiblockBE("fleshing_machine", FleshingMachineBlockEntity::new, ATTBlocks.FLESHING_MACHINE);

    public static <T extends BlockEntity & IEBlockInterfaces.IGeneralMultiblock> MultiblockBEType<T> registerMultiblockBE(String name, MultiblockBEType.BEWithTypeConstructor<T> make, Supplier<? extends Block> block)
    {
        return new MultiblockBEType<>(name, BLOCK_ENTITIES, make, block, state -> state.hasProperty(IEProperties.MULTIBLOCKSLAVE) && !state.getValue(IEProperties.MULTIBLOCKSLAVE));
    }
}