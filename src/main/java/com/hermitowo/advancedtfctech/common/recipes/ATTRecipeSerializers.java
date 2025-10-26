package com.hermitowo.advancedtfctech.common.recipes;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ATTRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, AdvancedTFCTech.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<ThresherRecipe>> THRESHER_SERIALIZER = register("thresher", ThresherRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<GristMillRecipe>> GRIST_MILL_SERIALIZER = register("grist_mill", GristMillRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<PowerLoomRecipe>> POWER_LOOM_SERIALIZER = register("power_loom", PowerLoomRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<BeamhouseRecipe>> BEAMHOUSE_SERIALIZER = register("beamhouse", BeamhouseRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, IERecipeSerializer<FleshingMachineRecipe>> FLESHING_MACHINE_SERIALIZER = register("fleshing_machine", FleshingMachineRecipe.Serializer::new);

    public static <T extends RecipeSerializer<?>> DeferredHolder<RecipeSerializer<?>, T> register(String name, Supplier<T> serializer)
    {
        return RECIPE_SERIALIZERS.register(name, serializer);
    }
}
