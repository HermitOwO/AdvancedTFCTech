package com.hermitowo.advancedtfctech.data.recipes;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.utils.TagUtils;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.register.IEItems;
import blusunrize.immersiveengineering.data.recipes.IERecipeProvider;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.recipes.outputs.AddMachineMadeModifier;
import com.hermitowo.advancedtfctech.common.recipes.outputs.CopyMachineMadeModifier;
import com.hermitowo.advancedtfctech.common.recipes.outputs.DoubleIfMachineMadeModifier;
import com.hermitowo.advancedtfctech.data.recipes.builder.BeamhouseRecipeBuilder;
import com.hermitowo.advancedtfctech.data.recipes.builder.FleshingMachineRecipeBuilder;
import com.hermitowo.advancedtfctech.data.recipes.builder.GristMillRecipeBuilder;
import com.hermitowo.advancedtfctech.data.recipes.builder.PowerLoomRecipeBuilder;
import com.hermitowo.advancedtfctech.data.recipes.builder.ThresherRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.common.recipes.outputs.CopyFoodModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;

public class ATTRecipes extends IERecipeProvider
{
    private final HashMap<String, Integer> pathCount = new HashMap<>();

    public ATTRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> provider)
    {
        super(output, provider);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput out)
    {
        crafting(out);

        anvil(out);

        thresher(out);
        gristMill(out);
        powerLoom(out);
        beamhouse(out);
        fleshingMachine(out);
    }

    private void crafting(RecipeOutput out)
    {
        shapedMisc(ATTItems.PIRN)
            .pattern("x")
            .pattern("y")
            .pattern("x")
            .define('x', IETags.getItemTag(IETags.treatedWood))
            .define('y', IETags.treatedStick)
            .unlockedBy("has_treated_planks", has(IETags.getItemTag(IETags.treatedWood)))
            .save(out, toRL(toPath(ATTItems.PIRN)));
        shapedMisc(ATTItems.FIBER_WINDED_PIRN)
            .pattern("xxx")
            .pattern("xyx")
            .pattern("xxx")
            .define('x', TFCItems.JUTE_FIBER)
            .define('y', ATTItems.PIRN)
            .unlockedBy("has_pirn", has(ATTItems.PIRN))
            .save(out, toRL(toPath(ATTItems.FIBER_WINDED_PIRN)));
        shapedMisc(ATTItems.SILK_WINDED_PIRN)
            .pattern("xxx")
            .pattern("xyx")
            .pattern("xxx")
            .define('x', Items.STRING)
            .define('y', ATTItems.PIRN)
            .unlockedBy("has_pirn", has(ATTItems.PIRN))
            .save(out, toRL(toPath(ATTItems.SILK_WINDED_PIRN)));
        shapedMisc(ATTItems.WOOL_WINDED_PIRN)
            .pattern("xxx")
            .pattern("xyx")
            .pattern("xxx")
            .define('x', TFCItems.WOOL_YARN)
            .define('y', ATTItems.PIRN)
            .unlockedBy("has_pirn", has(ATTItems.PIRN))
            .save(out, toRL(toPath(ATTItems.WOOL_WINDED_PIRN)));
        shapedMisc(ATTBlocks.FLESHING_MACHINE)
            .pattern("iei")
            .pattern("bmb")
            .pattern("ici")
            .define('i', IETags.getItemTag(IETags.getTagsFor(EnumMetals.IRON).sheetmetal))
            .define('e', IEItems.Ingredients.COMPONENT_ELECTRONIC)
            .define('b', TFCItems.BRASS_MECHANISMS)
            .define('m', IEItems.Ingredients.COMPONENT_IRON)
            .define('c', IEBlocks.MetalDecoration.LV_COIL)
            .unlockedBy("has_wrought_iron_ingot", has(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.INGOT)))
            .save(out, toRL(toPath(ATTBlocks.FLESHING_MACHINE)));
    }

    private void anvil(RecipeOutput out)
    {
        out.accept(toRL(toPath(ATTItems.FLESHING_BLADES)),
            new AnvilRecipe(
                Ingredient.of(TagUtils.createItemWrapper(ResourceLocation.fromNamespaceAndPath("c", "sheets/wrought_iron"))),
                3,
                List.of(ForgeRule.UPSET_THIRD_LAST, ForgeRule.SHRINK_SECOND_LAST, ForgeRule.HIT_LAST),
                true,
                ItemStackProvider.of(ATTItems.FLESHING_BLADES)
            ), null);
    }

    private void thresher(RecipeOutput out)
    {
        addGrainThresher(out, Food.BARLEY, Food.BARLEY_GRAIN);
        addGrainThresher(out, Food.MAIZE, Food.MAIZE_GRAIN);
        addGrainThresher(out, Food.OAT, Food.OAT_GRAIN);
        addGrainThresher(out, Food.RICE, Food.RICE_GRAIN);
        addGrainThresher(out, Food.RYE, Food.RYE_GRAIN);
        addGrainThresher(out, Food.WHEAT, Food.WHEAT_GRAIN);
    }

    private void gristMill(RecipeOutput out)
    {
        addGrainGristMill(out, Food.BARLEY_GRAIN, Food.BARLEY_FLOUR);
        addGrainGristMill(out, Food.MAIZE_GRAIN, Food.MAIZE_FLOUR);
        addGrainGristMill(out, Food.OAT_GRAIN, Food.OAT_FLOUR);
        addGrainGristMill(out, Food.RICE_GRAIN, Food.RICE_FLOUR);
        addGrainGristMill(out, Food.RYE_GRAIN, Food.RYE_FLOUR);
        addGrainGristMill(out, Food.WHEAT_GRAIN, Food.WHEAT_FLOUR);
    }

    private void powerLoom(RecipeOutput out)
    {
        PowerLoomRecipeBuilder.builder()
            .output(TFCItems.BURLAP_CLOTH, 8)
            .secondaryOutput(ATTItems.PIRN)
            .input(TFCItems.JUTE_FIBER, 48)
            .input(ATTItems.FIBER_WINDED_PIRN)
            .secondaryInput(TFCItems.JUTE_FIBER, 16)
            .inProgressTexture("advancedtfctech:block/multiblock/power_loom/burlap")
            .setTime(500)
            .setEnergy(40000)
            .build(out, toRL("power_loom/" + toPath(TFCItems.BURLAP_CLOTH)));
        PowerLoomRecipeBuilder.builder()
            .output(TFCItems.SILK_CLOTH, 4)
            .secondaryOutput(ATTItems.PIRN)
            .input(Items.STRING, 32)
            .input(ATTItems.SILK_WINDED_PIRN)
            .secondaryInput(Items.STRING, 16)
            .inProgressTexture("advancedtfctech:block/multiblock/power_loom/wool")
            .setTime(250)
            .setEnergy(20000)
            .build(out, toRL("power_loom/" + toPath(TFCItems.SILK_CLOTH)));
        PowerLoomRecipeBuilder.builder()
            .output(TFCItems.WOOL_CLOTH, 4)
            .secondaryOutput(ATTItems.PIRN)
            .input(TFCItems.WOOL_YARN, 32)
            .input(ATTItems.WOOL_WINDED_PIRN)
            .secondaryInput(TFCItems.WOOL_YARN, 16)
            .inProgressTexture("advancedtfctech:block/multiblock/power_loom/wool")
            .setTime(250)
            .setEnergy(20000)
            .build(out, toRL("power_loom/" + toPath(TFCItems.WOOL_CLOTH)));
    }

    private void beamhouse(RecipeOutput out)
    {
        for (HideItemType.Size size : HideItemType.Size.values())
        {
            final int amount = 300 + 100 * size.ordinal();
            final int time = amount - 100;
            final int energy = 20 * time;

            BeamhouseRecipeBuilder.builder()
                .output(ItemStackProvider.of(new ItemStack(TFCItems.HIDES.get(HideItemType.SOAKED).get(size)), AddMachineMadeModifier.INSTANCE))
                .input(TFCItems.HIDES.get(HideItemType.RAW).get(size))
                .fluidInput(SizedFluidIngredient.of(TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.LIMEWATER).getSource(), amount))
                .setTime(time)
                .setEnergy(energy)
                .build(out, toRL("beamhouse/" + toPath(TFCItems.HIDES.get(HideItemType.SOAKED).get(size))));
            BeamhouseRecipeBuilder.builder()
                .output(ItemStackProvider.of(new ItemStack(TFCItems.HIDES.get(HideItemType.PREPARED).get(size)), CopyMachineMadeModifier.INSTANCE))
                .input(TFCItems.HIDES.get(HideItemType.SCRAPED).get(size))
                .fluidInput(SizedFluidIngredient.of(Fluids.WATER, amount))
                .setTime(time)
                .setEnergy(energy)
                .build(out, toRL("beamhouse/" + toPath(TFCItems.HIDES.get(HideItemType.PREPARED).get(size))));
            BeamhouseRecipeBuilder.builder()
                .output(ItemStackProvider.of(new ItemStack(Items.LEATHER, 1 + size.ordinal()), DoubleIfMachineMadeModifier.INSTANCE))
                .input(TFCItems.HIDES.get(HideItemType.PREPARED).get(size))
                .fluidInput(SizedFluidIngredient.of(TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.TANNIN).getSource(), amount))
                .setTime(time)
                .setEnergy(energy)
                .build(out, toRL("beamhouse/" + size.name().toLowerCase(Locale.ROOT) + "_" + toPath(Items.LEATHER)));
        }
    }

    private void fleshingMachine(RecipeOutput out)
    {
        for (HideItemType.Size size : HideItemType.Size.values())
        {
            final int time = 100 + 50 * size.ordinal();
            final int energy = 20 * time;

            FleshingMachineRecipeBuilder.builder()
                .output(ItemStackProvider.of(new ItemStack(TFCItems.HIDES.get(HideItemType.SCRAPED).get(size)), CopyMachineMadeModifier.INSTANCE))
                .input(TFCItems.HIDES.get(HideItemType.SOAKED).get(size))
                .setTime(time)
                .setEnergy(energy)
                .build(out, toRL("fleshing_machine/" + toPath(TFCItems.HIDES.get(HideItemType.SCRAPED).get(size))));
        }
    }

    private void addGrainThresher(RecipeOutput out, Food raw, Food grain)
    {
        ThresherRecipeBuilder.builder()
            .output(ItemStackProvider.of(new ItemStack(TFCItems.FOOD.get(grain), 2), CopyFoodModifier.INSTANCE))
            .input(notRotten(TFCItems.FOOD.get(raw)))
            .secondaryOutput(new ItemStack(TFCItems.STRAW, 4))
            .setTime(80)
            .setEnergy(6400)
            .build(out, toRL("thresher/" + grain.getSerializedName()));
    }

    private void addGrainGristMill(RecipeOutput out, Food grain, Food flour)
    {
        GristMillRecipeBuilder.builder()
            .output(ItemStackProvider.of(new ItemStack(TFCItems.FOOD.get(flour), 2), CopyFoodModifier.INSTANCE))
            .input(notRotten(TFCItems.FOOD.get(grain)))
            .setTime(80)
            .setEnergy(6400)
            .build(out, toRL("grist_mill/" + flour.getSerializedName()));
    }

    @Override
    protected ResourceLocation toRL(String s)
    {
        if (!s.contains("/"))
            s = "crafting/" + s;
        if (pathCount.containsKey(s))
        {
            int count = pathCount.get(s) + 1;
            pathCount.put(s, count);
            return AdvancedTFCTech.rl(s + count);
        }
        pathCount.put(s, 1);
        return AdvancedTFCTech.rl(s);
    }

    private Ingredient notRotten(ItemLike input)
    {
        return AndIngredient.of(Ingredient.of(input), NotRottenIngredient.INSTANCE);
    }
}
