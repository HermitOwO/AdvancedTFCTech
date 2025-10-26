package com.hermitowo.advancedtfctech.common.capabilities;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.common.blocks.BlockCapabilityRegistration;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.MultiblockBEType;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ATTBlockCapabilities
{
    public static void register(RegisterCapabilitiesEvent event)
    {
        FleshingMachineBlockEntity.registerCapabilities(forType(event, ATTBlockEntities.FLESHING_MACHINE));
    }

    private static <BE extends BlockEntity> BlockCapabilityRegistration.BECapabilityRegistrar<BE> forType(
        RegisterCapabilitiesEvent ev, Supplier<BlockEntityType<BE>> type
    )
    {
        return new BlockCapabilityRegistration.BECapabilityRegistrar<>()
        {
            @Override
            public <C, T> void register(@NotNull BlockCapability<T, C> capability, @NotNull ICapabilityProvider<? super BE, C, T> provider)
            {
                ev.registerBlockEntity(capability, type.get(), provider);
            }
        };
    }

    private static <BE extends BlockEntity & IEBlockInterfaces.IGeneralMultiblock> BlockCapabilityRegistration.BECapabilityRegistrar<BE> forType(
        RegisterCapabilitiesEvent ev, MultiblockBEType<BE> type
    )
    {
        return new BlockCapabilityRegistration.BECapabilityRegistrar<>()
        {
            @Override
            public <C, T> void register(@NotNull BlockCapability<T, C> capability, @NotNull ICapabilityProvider<? super BE, C, T> provider)
            {
                ev.registerBlockEntity(capability, type.dummy(), provider);
                ev.registerBlockEntity(capability, type.master(), provider);
            }
        };
    }
}
