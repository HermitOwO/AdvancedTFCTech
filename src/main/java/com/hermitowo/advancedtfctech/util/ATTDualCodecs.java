package com.hermitowo.advancedtfctech.util;

import malte0811.dualcodecs.DualCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public final class ATTDualCodecs
{
    public static final DualCodec<RegistryFriendlyByteBuf, ItemStackProvider> ITEM_STACK_PROVIDER = new DualCodec<>(
        ItemStackProvider.CODEC, ItemStackProvider.STREAM_CODEC
    );
}
