package com.hermitowo.advancedtfctech.common.recipes;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    public static final RegistryObject<IERecipeSerializer<ThresherRecipe>> THRESHER_SERIALIZER = register("thresher", ThresherRecipe.Serializer::new);
    public static final RegistryObject<IERecipeSerializer<GristMillRecipe>> GRIST_MILL_SERIALIZER = register("grist_mill", GristMillRecipe.Serializer::new);
    public static final RegistryObject<IERecipeSerializer<PowerLoomRecipe>> POWER_LOOM_SERIALIZER = register("power_loom", PowerLoomRecipe.Serializer::new);
    public static final RegistryObject<IERecipeSerializer<BeamhouseRecipe>> BEAMHOUSE_SERIALIZER = register("beamhouse", BeamhouseRecipe.Serializer::new);
    public static final RegistryObject<IERecipeSerializer<FleshingMachineRecipe>> FLESHING_MACHINE_SERIALIZER = register("fleshing_machine", FleshingMachineRecipe.Serializer::new);

    public static <T extends RecipeSerializer<?>> RegistryObject<T> register(String name, Supplier<T> serializer)
    {
        return RECIPE_SERIALIZERS.register(name, serializer);
    }
}
