package com.hermitowo.advancedtfctech.config;

import java.util.function.Function;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTServerConfig
{
    public final DoubleValue thresher_energyModifier;
    public final DoubleValue thresher_timeModifier;
    public final DoubleValue gristMill_energyModifier;
    public final DoubleValue gristMill_timeModifier;
    public final DoubleValue powerLoom_energyModifier;
    public final DoubleValue powerLoom_timeModifier;
    public final DoubleValue beamhouse_energyModifier;
    public final DoubleValue beamhouse_timeModifier;
    public final IntValue fleshingMachine_bladesDamage;
    public final BooleanValue enablePowerLoomDebug;
    public final BooleanValue enableFleshingMachineDebug;


    ATTServerConfig(Builder innerBuilder)
    {
        Function<String, Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

        innerBuilder.push("general");

        thresher_energyModifier = builder.apply("thresher_energyModifier").comment("A modifier to apply to the energy costs of every Thresher recipe.").defineInRange("thresher_energyModifier", 1.0, 0, Double.MAX_VALUE);
        thresher_timeModifier = builder.apply("thresher_timeModifier").comment("A modifier to apply to the time of every Thresher recipe.").defineInRange("thresher_timeModifier", 1.0, 1.0, Double.MAX_VALUE);
        gristMill_energyModifier = builder.apply("gristMill_energyModifier").comment("A modifier to apply to the energy costs of every Grist Mill recipe.").defineInRange("gristMill_energyModifier", 1.0, 0, Double.MAX_VALUE);
        gristMill_timeModifier = builder.apply("gristMill_timeModifier").comment("A modifier to apply to the time of every Grist Mill recipe.").defineInRange("gristMill_timeModifier", 1.0, 1.0, Double.MAX_VALUE);
        powerLoom_energyModifier = builder.apply("powerLoom_energyModifier").comment("A modifier to apply to the energy costs of every Power Loom recipe.").defineInRange("powerLoom_energyModifier", 1.0, 0, Double.MAX_VALUE);
        powerLoom_timeModifier = builder.apply("powerLoom_timeModifier").comment("A modifier to apply to the time of every Power Loom recipe.").defineInRange("powerLoom_timeModifier", 1.0, 1.0, Double.MAX_VALUE);
        beamhouse_energyModifier = builder.apply("beamhouse_energyModifier").comment("A modifier to apply to the energy costs of every Beamhouse recipe.").defineInRange("beamhouse_energyModifier", 1.0, 0, Double.MAX_VALUE);
        beamhouse_timeModifier = builder.apply("beamhouse_timeModifier").comment("A modifier to apply to the time of every Beamhouse recipe.").defineInRange("beamhouse_timeModifier", 1.0, 1.0, Double.MAX_VALUE);
        fleshingMachine_bladesDamage = builder.apply("fleshingMachine_bladesDamage").comment("The maximum amount of damage Fleshing Blades can take. While the fleshing machine is working, the blades sustain 1 damage per tick, so this is effectively the lifetime in ticks.").defineInRange("fleshingMachine_bladesDamage", 20000, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("debug");

        enablePowerLoomDebug = builder.apply("enablePowerLoomDebug").comment("If true, a GUI can be opened up by rightclicking the Power Loom with a Pirn.").define("enablePowerLoomDebug", false);
        enableFleshingMachineDebug = builder.apply("enableFleshingMachineDebug").comment("If true, a GUI can be opened up by rightclicking the Fleshing Machine.").define("enableFleshingMachineDebug", false);

        innerBuilder.pop();
    }
}