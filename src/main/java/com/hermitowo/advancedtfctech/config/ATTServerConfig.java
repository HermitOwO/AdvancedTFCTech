package com.hermitowo.advancedtfctech.config;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import com.hermitowo.advancedtfctech.common.recipes.GristMillRecipe;
import com.hermitowo.advancedtfctech.common.recipes.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.common.recipes.ThresherRecipe;

import net.dries007.tfc.config.BaseConfig;

public class ATTServerConfig extends BaseConfig
{
    public final MultiblockRecipe.RecipeMultiplier thresherConfig;
    public final MultiblockRecipe.RecipeMultiplier gristMillConfig;
    public final MultiblockRecipe.RecipeMultiplier powerLoomConfig;
    public final MultiblockRecipe.RecipeMultiplier beamhouseConfig;
    public final Supplier<Integer> fleshingMachine_bladesDamage;
    public final Supplier<Boolean> enablePowerLoomDebug;
    public final Supplier<Boolean> enableFleshingMachineDebug;


    ATTServerConfig(ConfigBuilder builder)
    {
        builder.push("general");

        thresherConfig = addMachineEnergyTimeModifiers(builder, "thresher");
        gristMillConfig = addMachineEnergyTimeModifiers(builder, "grist mill");
        powerLoomConfig = addMachineEnergyTimeModifiers(builder, "power loom");
        beamhouseConfig = addMachineEnergyTimeModifiers(builder, "beamhouse");
        fleshingMachine_bladesDamage = builder.comment("The maximum amount of damage Fleshing Blades can take. While the fleshing machine is working, the blades sustain 1 damage per tick, so this is effectively the lifetime in ticks.").define("fleshingMachine_bladesDamage", 20000, 1, Integer.MAX_VALUE);

        builder.swap("debug");

        enablePowerLoomDebug = builder.comment("If true, a GUI can be opened up by rightclicking the Power Loom with a Pirn.").define("enablePowerLoomDebug", false);
        enableFleshingMachineDebug = builder.comment("If true, a GUI can be opened up by rightclicking the Fleshing Machine.").define("enableFleshingMachineDebug", false);

        builder.pop();
    }

    public void populateAPI()
    {
        ThresherRecipe.MULTIPLIERS.setValue(thresherConfig);
        GristMillRecipe.MULTIPLIERS.setValue(gristMillConfig);
        PowerLoomRecipe.MULTIPLIERS.setValue(powerLoomConfig);
        BeamhouseRecipe.MULTIPLIERS.setValue(beamhouseConfig);
    }

    private MultiblockRecipe.RecipeMultiplier addMachineEnergyTimeModifiers(ConfigBuilder builder, String machine)
    {
        return addMachineEnergyTimeModifiers(builder, machine, true);
    }

    private MultiblockRecipe.RecipeMultiplier addMachineEnergyTimeModifiers(ConfigBuilder builder, String machine, boolean popCategory)
    {
        builder.push(machine.replace(' ', '_'));
        Supplier<Double> energy = builder
            .comment("A modifier to apply to the energy costs of every " + machine + " recipe")
            .define("energyModifier", 1, 1e-3, 1e3);
        Supplier<Double> time = builder
            .comment("A modifier to apply to the time of every " + machine + " recipe")
            .define("timeModifier", 1, 1e-3, 1e3);
        if (popCategory)
            builder.pop();
        return new MultiblockRecipe.RecipeMultiplier(energy::get, time::get);
    }
}