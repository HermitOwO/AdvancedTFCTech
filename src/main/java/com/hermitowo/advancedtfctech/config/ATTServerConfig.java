package com.hermitowo.advancedtfctech.config;

import java.util.function.Function;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTServerConfig
{
    public final ConfigValue<Double> thresher_energyModifier;
    public final ConfigValue<Double> thresher_timeModifier;
    public final ConfigValue<Double> gristMill_energyModifier;
    public final ConfigValue<Double> gristMill_timeModifier;
    public final ConfigValue<Double> powerLoom_energyModifier;
    public final ConfigValue<Double> powerLoom_timeModifier;
    public final BooleanValue enablePowerLoomDebug;

    ATTServerConfig(Builder innerBuilder)
    {
        Function<String, Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

        innerBuilder.push("general");

        thresher_energyModifier = builder.apply("thresher_energyModifier").comment("A modifier to apply to the energy costs of every Thresher recipe.").define("thresher_energyModifier", 1.0);
        thresher_timeModifier = builder.apply("thresher_timeModifier").comment("A modifier to apply to the time of every Thresher recipe. Can't be lower than 1.").define("thresher_timeModifier", 1.0);
        gristMill_energyModifier = builder.apply("gristMill_energyModifier").comment("A modifier to apply to the energy costs of every Grist Mill recipe.").define("gristMill_energyModifier", 1.0);
        gristMill_timeModifier = builder.apply("gristMill_timeModifier").comment("A modifier to apply to the time of every Grist Mill recipe. Can't be lower than 1.").define("gristMill_timeModifier", 1.0);
        powerLoom_energyModifier = builder.apply("powerLoom_energyModifier").comment("A modifier to apply to the energy costs of every Power Loom recipe.").define("powerLoom_energyModifier", 1.0);
        powerLoom_timeModifier = builder.apply("powerLoom_timeModifier").comment("A modifier to apply to the time of every Power Loom recipe. Can't be lower than 1.").define("powerLoom_timeModifier", 1.0);

        innerBuilder.pop().push("debug");

        enablePowerLoomDebug = builder.apply("enablePowerLoomDebug").comment("If true, a GUI can be opened up by rightclicking the Power Loom with a Pirn.").define("enablePowerLoomDebug", false);

        innerBuilder.pop();
    }
}