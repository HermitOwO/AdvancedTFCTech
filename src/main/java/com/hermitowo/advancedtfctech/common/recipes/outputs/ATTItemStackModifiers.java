package com.hermitowo.advancedtfctech.common.recipes.outputs;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;

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
        ItemStackModifiers.register(AdvancedTFCTech.rl(name), serializer);
    }
}
