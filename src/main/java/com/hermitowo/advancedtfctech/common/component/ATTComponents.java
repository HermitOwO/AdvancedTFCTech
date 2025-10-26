package com.hermitowo.advancedtfctech.common.component;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.component.TFCComponents.Id;

@SuppressWarnings("SameParameterValue")
public class ATTComponents
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AdvancedTFCTech.MOD_ID);

    public static final Id<Boolean> MACHINE_MADE = register("machine_made", Codec.BOOL, ByteBufCodecs.BOOL);

    private static <T> Id<T> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new Id<>(COMPONENTS.register(name, () -> new DataComponentType.Builder<T>()
            .persistent(codec)
            .networkSynchronized(streamCodec)
            .build()));
    }
}
