package com.hermitowo.advancedtfctech.common.recipes.outputs;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifierType;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;
import net.dries007.tfc.util.registry.RegistryHolder;

public class ATTItemStackModifiers
{
    public static final DeferredRegister<ItemStackModifierType<?>> TYPES = DeferredRegister.create(ItemStackModifiers.KEY, AdvancedTFCTech.MOD_ID);

    public static final Id<AddMachineMadeModifier> ADD_MACHINE_MADE = register("add_machine_made", AddMachineMadeModifier.INSTANCE);
    public static final Id<CopyMachineMadeModifier> COPY_MACHINE_MADE = register("copy_machine_made", CopyMachineMadeModifier.INSTANCE);
    public static final Id<DoubleIfMachineMadeModifier> DOUBLE_IF_MACHINE_MADE = register("double_if_machine_made", DoubleIfMachineMadeModifier.INSTANCE);

    private static <T extends ItemStackModifier> Id<T> register(String name, T singleInstance)
    {
        return new Id<>(TYPES.register(name, () -> new ItemStackModifierType<>(MapCodec.unit(singleInstance), StreamCodec.unit(singleInstance))));
    }

    record Id<T extends ItemStackModifier>(DeferredHolder<ItemStackModifierType<?>, ItemStackModifierType<T>> holder)
        implements RegistryHolder<ItemStackModifierType<?>, ItemStackModifierType<T>> {}
}
