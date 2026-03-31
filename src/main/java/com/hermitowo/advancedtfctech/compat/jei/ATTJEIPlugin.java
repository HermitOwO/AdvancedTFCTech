package com.hermitowo.advancedtfctech.compat.jei;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.client.screen.BeamhouseScreen;
import com.hermitowo.advancedtfctech.client.screen.GristMillScreen;
import com.hermitowo.advancedtfctech.client.screen.ThresherScreen;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import com.hermitowo.advancedtfctech.common.recipes.ATTRecipeTypes;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import com.hermitowo.advancedtfctech.common.recipes.FleshingMachineRecipe;
import com.hermitowo.advancedtfctech.common.recipes.GristMillRecipe;
import com.hermitowo.advancedtfctech.common.recipes.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.common.recipes.ThresherRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.client.ClientHelpers;

@SuppressWarnings("unused")
@JeiPlugin
public class ATTJEIPlugin implements IModPlugin
{
    @Override
    public ResourceLocation getPluginUid()
    {
        return AdvancedTFCTech.rl("jei");
    }

    public static final RecipeType<RecipeHolder<ThresherRecipe>> THRESHER = type("thresher", ThresherRecipe.class);
    public static final RecipeType<RecipeHolder<GristMillRecipe>> GRIST_MILL = type("grist_mill", GristMillRecipe.class);
    public static final RecipeType<RecipeHolder<PowerLoomRecipe>> POWER_LOOM = type("power_loom", PowerLoomRecipe.class);
    public static final RecipeType<RecipeHolder<BeamhouseRecipe>> BEAMHOUSE = type("beamhouse", BeamhouseRecipe.class);
    public static final RecipeType<RecipeHolder<FleshingMachineRecipe>> FLESHING_MACHINE = type("fleshing_machine", FleshingMachineRecipe.class);

    private static <T extends Recipe<?>> RecipeType<RecipeHolder<T>> type(String name, Class<T> kind)
    {
        return RecipeType.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(AdvancedTFCTech.MOD_ID, name));
    }

    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> recipes(Supplier<net.minecraft.world.item.crafting.RecipeType<T>> type)
    {
        return recipes(type, e -> true);
    }

    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> recipes(Supplier<net.minecraft.world.item.crafting.RecipeType<T>> type, Predicate<T> filter)
    {
        return ClientHelpers.getLevelOrThrow().getRecipeManager()
            .getAllRecipesFor(type.get())
            .stream()
            .filter(holder -> filter.test(holder.value()))
            .toList();
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration r)
    {
        IGuiHelper guiHelper = r.getJeiHelpers().getGuiHelper();

        r.addRecipeCategories(
            new ThresherRecipeCategory(THRESHER, guiHelper),
            new GristMillRecipeCategory(GRIST_MILL, guiHelper),
            new PowerLoomRecipeCategory(POWER_LOOM, guiHelper),
            new BeamhouseRecipeCategory(BEAMHOUSE, guiHelper),
            new FleshingMachineRecipeCategory(FLESHING_MACHINE, guiHelper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration r)
    {
        r.addRecipes(THRESHER, recipes(ATTRecipeTypes.THRESHER));
        r.addRecipes(GRIST_MILL, recipes(ATTRecipeTypes.GRIST_MILL));
        r.addRecipes(POWER_LOOM, recipes(ATTRecipeTypes.POWER_LOOM));
        r.addRecipes(BEAMHOUSE, recipes(ATTRecipeTypes.BEAMHOUSE));
        r.addRecipes(FLESHING_MACHINE, recipes(ATTRecipeTypes.FLESHING_MACHINE));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r)
    {
        cat(r, ATTMultiblockLogic.THRESHER.block(), THRESHER);
        cat(r, ATTMultiblockLogic.GRIST_MILL.block(), GRIST_MILL);
        cat(r, ATTMultiblockLogic.POWER_LOOM.block(), POWER_LOOM);
        cat(r, ATTMultiblockLogic.BEAMHOUSE.block(), BEAMHOUSE);
        cat(r, ATTBlocks.FLESHING_MACHINE, FLESHING_MACHINE);
    }

    private static void cat(IRecipeCatalystRegistration r, Supplier<? extends Block> supplier, RecipeType<?> type)
    {
        r.addRecipeCatalyst(new ItemStack(supplier.get()), type);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration r)
    {
        r.addRecipeClickArea(ThresherScreen.class, 77, 54, 22, 17, THRESHER);
        r.addRecipeClickArea(GristMillScreen.class, 77, 54, 22, 17, GRIST_MILL);
        r.addRecipeClickArea(BeamhouseScreen.class, 17, 69, 26, 18, BEAMHOUSE);
    }
}