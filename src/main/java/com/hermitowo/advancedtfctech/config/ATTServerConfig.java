package com.hermitowo.advancedtfctech.config;

import java.lang.reflect.Field;
import java.util.function.Function;
import com.electronwill.nightconfig.core.Config;
import com.google.common.base.Preconditions;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

@EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)
public class ATTServerConfig
{
    public static final General GENERAL;

    public static class General
    {
        public final ForgeConfigSpec.ConfigValue<Double> powerLoom_energyModifier;
        public final ForgeConfigSpec.ConfigValue<Double> powerLoom_timeModifier;
        public final ForgeConfigSpec.ConfigValue<Double> thresher_energyModifier;
        public final ForgeConfigSpec.ConfigValue<Double> thresher_timeModifier;
        public final ForgeConfigSpec.ConfigValue<Double> gristMill_energyModifier;
        public final ForgeConfigSpec.ConfigValue<Double> gristMill_timeModifier;

        General(ForgeConfigSpec.Builder innerBuilder)
        {
            Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.server." + name);

            innerBuilder.push("general");

            powerLoom_energyModifier = builder.apply("powerLoom_energyModifier").comment("A modifier to apply to the energy costs of every Power Loom recipe.").define("powerLoom_energyModifier", 1.0);
            powerLoom_timeModifier = builder.apply("powerLoom_timeModifier").comment("A modifier to apply to the time of every Power Loom recipe. Can't be lower than 1.").define("powerLoom_timeModifier", 1.0);
            thresher_energyModifier = builder.apply("thresher_energyModifier").comment("A modifier to apply to the energy costs of every Thresher recipe.").define("thresher_energyModifier", 1.0);
            thresher_timeModifier = builder.apply("thresher_timeModifier").comment("A modifier to apply to the time of every Thresher recipe. Can't be lower than 1.").define("thresher_timeModifier", 1.0);
            gristMill_energyModifier = builder.apply("gristMill_energyModifier").comment("A modifier to apply to the energy costs of every Grist Mill recipe.").define("gristMill_energyModifier", 1.0);
            gristMill_timeModifier = builder.apply("gristMill_timeModifier").comment("A modifier to apply to the time of every Grist Mill recipe. Can't be lower than 1.").define("gristMill_timeModifier", 1.0);

            innerBuilder.pop();
        }
    }

    public static final ForgeConfigSpec ALL;

    static{
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        GENERAL = new General(builder);

        ALL = builder.build();
    }

    private static Config rawConfig;
    public static Config getRawConfig()
    {
        if(rawConfig == null)
        {
            try
            {
                Field childConfig = ForgeConfigSpec.class.getDeclaredField("childConfig");
                childConfig.setAccessible(true);
                rawConfig = (Config) childConfig.get(ALL);
                Preconditions.checkNotNull(rawConfig);
            }
            catch(Exception x)
            {
                throw new RuntimeException(x);
            }
        }
        return rawConfig;
    }
}