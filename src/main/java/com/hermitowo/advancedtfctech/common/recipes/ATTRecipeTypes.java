package com.hermitowo.advancedtfctech.common.recipes;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, MOD_ID);

    public static final TypeWithClass<ThresherRecipe> THRESHER = register("thresher", ThresherRecipe.class);
    public static final TypeWithClass<GristMillRecipe> GRIST_MILL = register("grist_mill", GristMillRecipe.class);
    public static final TypeWithClass<PowerLoomRecipe> POWER_LOOM = register("power_loom", PowerLoomRecipe.class);
    public static final TypeWithClass<BeamhouseRecipe> BEAMHOUSE = register("beamhouse", BeamhouseRecipe.class);
    public static final TypeWithClass<FleshingMachineRecipe> FLESHING_MACHINE = register("fleshing_machine", FleshingMachineRecipe.class);

    private static <T extends Recipe<?>> TypeWithClass<T> register(String name, Class<T> type)
    {
        RegistryObject<RecipeType<T>> regObj = RECIPE_TYPES.register(name, () -> new RecipeType<>()
        {
        });
        return new TypeWithClass<>(regObj, type);
    }

    public record TypeWithClass<T extends Recipe<?>>(RegistryObject<RecipeType<T>> type, Class<T> recipeClass) implements Supplier<RecipeType<T>>
    {
        public RecipeType<T> get()
        {
            return type.get();
        }
    }
}