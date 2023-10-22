package com.hermitowo.advancedtfctech.common.recipes.outputs;

import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTItemStackModifiers
{
    public static void registerItemStackModifierTypes()
    {
        register("add_tag", AddTagModifier.Serializer.INSTANCE);
        register("copy_tag", CopyTagModifier.Serializer.INSTANCE);
        register("double_if_has_tag", DoubleIfHasTagModifier.Serializer.INSTANCE);
    }

    private static void register(String name, ItemStackModifier.Serializer<?> serializer)
    {
        ItemStackModifiers.register(new ResourceLocation(MOD_ID, name), serializer);
    }
}
