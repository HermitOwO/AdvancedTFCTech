package com.hermitowo.advancedtfctech.common.recipes;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ATTRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, AdvancedTFCTech.MOD_ID);

    public static final IERecipeTypes.TypeWithClass<ThresherRecipe> THRESHER = register("thresher", ThresherRecipe.class);
    public static final IERecipeTypes.TypeWithClass<GristMillRecipe> GRIST_MILL = register("grist_mill", GristMillRecipe.class);
    public static final IERecipeTypes.TypeWithClass<PowerLoomRecipe> POWER_LOOM = register("power_loom", PowerLoomRecipe.class);
    public static final IERecipeTypes.TypeWithClass<BeamhouseRecipe> BEAMHOUSE = register("beamhouse", BeamhouseRecipe.class);
    public static final IERecipeTypes.TypeWithClass<FleshingMachineRecipe> FLESHING_MACHINE = register("fleshing_machine", FleshingMachineRecipe.class);

    private static <T extends Recipe<?>> IERecipeTypes.TypeWithClass<T> register(String name, Class<T> type)
    {
        RegistryObject<RecipeType<T>> regObj = RECIPE_TYPES.register(name, () -> new RecipeType<>()
        {
        });
        return new IERecipeTypes.TypeWithClass<>(regObj, type);
    }
}