package com.hermitowo.advancedtfctech.compat.jei;

import java.util.List;
import java.util.function.Supplier;
import com.hermitowo.advancedtfctech.common.recipes.ATTRecipeTypes;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import com.hermitowo.advancedtfctech.common.recipes.GristMillRecipe;
import com.hermitowo.advancedtfctech.common.recipes.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.common.recipes.FleshingMachineRecipe;
import com.hermitowo.advancedtfctech.common.recipes.ThresherRecipe;
import com.hermitowo.advancedtfctech.client.screen.BeamhouseScreen;
import com.hermitowo.advancedtfctech.client.screen.GristMillScreen;
import com.hermitowo.advancedtfctech.client.screen.ThresherScreen;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

@SuppressWarnings("unused")
@JeiPlugin
public class ATTJEIPlugin implements IModPlugin
{
    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation(MOD_ID, "jei");
    }

    private static <C extends Container, T extends Recipe<C>> List<T> getRecipes(net.minecraft.world.item.crafting.RecipeType<T> type)
    {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;
        return level.getRecipeManager().getAllRecipesFor(type);
    }

    public static final RecipeType<ThresherRecipe> THRESHER = type("thresher", ThresherRecipe.class);
    public static final RecipeType<GristMillRecipe> GRIST_MILL = type("grist_mill", GristMillRecipe.class);
    public static final RecipeType<PowerLoomRecipe> POWER_LOOM = type("power_loom", PowerLoomRecipe.class);
    public static final RecipeType<BeamhouseRecipe> BEAMHOUSE = type("beamhouse", BeamhouseRecipe.class);
    public static final RecipeType<FleshingMachineRecipe> FLESHING_MACHINE = type("fleshing_machine", FleshingMachineRecipe.class);

    private static <T> RecipeType<T> type(String name, Class<T> tClass)
    {
        return RecipeType.create(MOD_ID, name, tClass);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration r)
    {
        IGuiHelper guiHelper = r.getJeiHelpers().getGuiHelper();

        r.addRecipeCategories(new ThresherRecipeCategory(THRESHER, guiHelper));
        r.addRecipeCategories(new GristMillRecipeCategory(GRIST_MILL, guiHelper));
        r.addRecipeCategories(new PowerLoomRecipeCategory(POWER_LOOM, guiHelper));
        r.addRecipeCategories(new BeamhouseRecipeCategory(BEAMHOUSE, guiHelper));
        r.addRecipeCategories(new FleshingMachineRecipeCategory(FLESHING_MACHINE, guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration r)
    {
        r.addRecipes(THRESHER, getRecipes(ATTRecipeTypes.THRESHER.get()));
        r.addRecipes(GRIST_MILL, getRecipes(ATTRecipeTypes.GRIST_MILL.get()));
        r.addRecipes(POWER_LOOM, getRecipes(ATTRecipeTypes.POWER_LOOM.get()));
        r.addRecipes(BEAMHOUSE, getRecipes(ATTRecipeTypes.BEAMHOUSE.get()));
        r.addRecipes(FLESHING_MACHINE, getRecipes(ATTRecipeTypes.FLESHING_MACHINE.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r)
    {
        cat(r, ATTBlocks.Multiblocks.THRESHER, THRESHER);
        cat(r, ATTBlocks.Multiblocks.GRIST_MILL, GRIST_MILL);
        cat(r, ATTBlocks.Multiblocks.POWER_LOOM, POWER_LOOM);
        cat(r, ATTBlocks.Multiblocks.BEAMHOUSE, BEAMHOUSE);
        cat(r, ATTBlocks.Blocks.FLESHING_MACHINE, FLESHING_MACHINE);
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