package com.hermitowo.advancedtfctech.config;

import java.util.function.Function;
import blusunrize.immersiveengineering.api.crafting.MultiblockRecipe;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import com.hermitowo.advancedtfctech.common.recipes.GristMillRecipe;
import com.hermitowo.advancedtfctech.common.recipes.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.common.recipes.ThresherRecipe;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTServerConfig
{
    public final IEServerConfig.Machines.MachineRecipeConfig<ThresherRecipe> thresherConfig;
    public final IEServerConfig.Machines.MachineRecipeConfig<GristMillRecipe> gristMillConfig;
    public final IEServerConfig.Machines.MachineRecipeConfig<PowerLoomRecipe> powerLoomConfig;
    public final IEServerConfig.Machines.MachineRecipeConfig<BeamhouseRecipe> beamhouseConfig;
    public final IntValue fleshingMachine_bladesDamage;
    public final BooleanValue enablePowerLoomDebug;
    public final BooleanValue enableFleshingMachineDebug;


    ATTServerConfig(Builder innerBuilder)
    {
        Function<String, Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

        innerBuilder.push("general");

        thresherConfig = addMachineEnergyTimeModifiers(innerBuilder, "thresher");
        gristMillConfig = addMachineEnergyTimeModifiers(innerBuilder, "grist mill");
        powerLoomConfig = addMachineEnergyTimeModifiers(innerBuilder, "power loom");
        beamhouseConfig = addMachineEnergyTimeModifiers(innerBuilder, "beamhouse");
        fleshingMachine_bladesDamage = builder.apply("fleshingMachine_bladesDamage").comment("The maximum amount of damage Fleshing Blades can take. While the fleshing machine is working, the blades sustain 1 damage per tick, so this is effectively the lifetime in ticks.").defineInRange("fleshingMachine_bladesDamage", 20000, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("debug");

        enablePowerLoomDebug = builder.apply("enablePowerLoomDebug").comment("If true, a GUI can be opened up by rightclicking the Power Loom with a Pirn.").define("enablePowerLoomDebug", false);
        enableFleshingMachineDebug = builder.apply("enableFleshingMachineDebug").comment("If true, a GUI can be opened up by rightclicking the Fleshing Machine.").define("enableFleshingMachineDebug", false);

        innerBuilder.pop();
    }

    private <T extends MultiblockRecipe> IEServerConfig.Machines.MachineRecipeConfig<T> addMachineEnergyTimeModifiers(Builder builder, String machine)
    {
        builder.push(machine.replace(' ', '_'));
        DoubleValue energy = builder
            .comment("A modifier to apply to the energy costs of every " + machine + " recipe")
            .defineInRange("energyModifier", 1, 1e-3, 1e3);
        DoubleValue time = builder
            .comment("A modifier to apply to the time of every " + machine + " recipe")
            .defineInRange("timeModifier", 1, 1e-3, 1e3);
        builder.pop();
        return new IEServerConfig.Machines.MachineRecipeConfig<>(energy, time);
    }
}