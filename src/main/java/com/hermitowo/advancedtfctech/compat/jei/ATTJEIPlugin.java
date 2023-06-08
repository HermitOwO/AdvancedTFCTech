package com.hermitowo.advancedtfctech.compat.jei;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.hermitowo.advancedtfctech.api.crafting.ATTRecipeTypes;
import com.hermitowo.advancedtfctech.api.crafting.GristMillRecipe;
import com.hermitowo.advancedtfctech.api.crafting.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.api.crafting.ThresherRecipe;
import com.hermitowo.advancedtfctech.client.screen.GristMillScreen;
import com.hermitowo.advancedtfctech.client.screen.ThresherScreen;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
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

@JeiPlugin
public class ATTJEIPlugin implements IModPlugin
{
    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation(MOD_ID, "jei");
    }

    public static IRecipeSlotTooltipCallback notConsumedTooltipCallback = new NotConsumedTooltipCallback();

    private static <C extends Container, T extends Recipe<C>> List<T> getRecipes(net.minecraft.world.item.crafting.RecipeType<T> type)
    {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;
        return level.getRecipeManager().getAllRecipesFor(type);
    }

    private static <C extends Container, T extends Recipe<C>> List<T> getRecipes(net.minecraft.world.item.crafting.RecipeType<T> type, Predicate<T> filter)
    {
        return getRecipes(type).stream().filter(filter).collect(Collectors.toList());
    }

    public static final RecipeType<ThresherRecipe> THRESHER = type("thresher", ThresherRecipe.class);
    public static final RecipeType<GristMillRecipe> GRIST_MILL = type("grist_mill", GristMillRecipe.class);
    public static final RecipeType<PowerLoomRecipe> POWER_LOOM = type("power_loom", PowerLoomRecipe.class);

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
    }

    @Override
    public void registerRecipes(IRecipeRegistration r)
    {
        r.addRecipes(THRESHER, getRecipes(ATTRecipeTypes.THRESHER.get()));
        r.addRecipes(GRIST_MILL, getRecipes(ATTRecipeTypes.GRIST_MILL.get()));
        r.addRecipes(POWER_LOOM, getRecipes(ATTRecipeTypes.POWER_LOOM.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r)
    {
        cat(r, ATTBlocks.Multiblocks.THRESHER, THRESHER);
        cat(r, ATTBlocks.Multiblocks.GRIST_MILL, GRIST_MILL);
        cat(r, ATTBlocks.Multiblocks.POWER_LOOM, POWER_LOOM);
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
    }
}