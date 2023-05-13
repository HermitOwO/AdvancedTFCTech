package com.hermitowo.advancedtfctech.common.crafting;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import com.hermitowo.advancedtfctech.api.crafting.GristMillRecipe;
import com.hermitowo.advancedtfctech.api.crafting.ThresherRecipe;
import com.hermitowo.advancedtfctech.common.crafting.serializers.GristMillRecipeSerializer;
import com.hermitowo.advancedtfctech.common.crafting.serializers.ThresherRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    public static final RegistryObject<IERecipeSerializer<ThresherRecipe>> THRESHER_SERIALIZER = register("thresher", ThresherRecipeSerializer::new);
    public static final RegistryObject<IERecipeSerializer<GristMillRecipe>> GRIST_MILL_SERIALIZER = register("grist_mill", GristMillRecipeSerializer::new);

    public static <T extends RecipeSerializer<?>> RegistryObject<T> register(String name, Supplier<T> serializer)
    {
        return RECIPE_SERIALIZERS.register(name, serializer);
    }
}
