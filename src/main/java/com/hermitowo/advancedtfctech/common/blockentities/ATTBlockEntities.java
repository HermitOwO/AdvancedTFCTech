package com.hermitowo.advancedtfctech.common.blockentities;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MOD_ID);

    public static final MultiblockBEType<ThresherBlockEntity> THRESHER = registerMultiblockBE("thresher", ThresherBlockEntity::new, ATTBlocks.Multiblocks.THRESHER);
    public static final MultiblockBEType<GristMillBlockEntity> GRIST_MILL = registerMultiblockBE("grist_mill", GristMillBlockEntity::new, ATTBlocks.Multiblocks.GRIST_MILL);

    public static <T extends BlockEntity & IEBlockInterfaces.IGeneralMultiblock> MultiblockBEType<T> registerMultiblockBE(String name, MultiblockBEType.BEWithTypeConstructor<T> factory, Supplier<? extends Block> valid)
    {
        return new MultiblockBEType<>(name, BLOCK_ENTITIES, factory, valid, state -> state.hasProperty(IEProperties.MULTIBLOCKSLAVE) && !state.getValue(IEProperties.MULTIBLOCKSLAVE));
    }
}