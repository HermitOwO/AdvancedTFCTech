package com.hermitowo.advancedtfctech.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTClientConfig
{
    public final ConfigValue<List<? extends List<? extends String>>> additionalPowerLoomPirnTextures;
    public final ConfigValue<List<? extends List<? extends String>>> additionalFleshingMachineTextures;

    ATTClientConfig(Builder innerBuilder)
    {
        Function<String, Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.client." + name);

        innerBuilder.push("compatibility");

        additionalPowerLoomPirnTextures = builder.apply("additionalPowerLoomPirnTextures").comment(
            "If you use a custom \"pirn\" in a Power Loom recipe (the second item in input list), the registry name of the pirn item and the textures need to be added to this list.",
            "Example: additionalPowerLoomPirnTextures = [[\"domain:registry_name1\", \"domain:texture_location1\"], [\"domain:registry_namnds List<? extends String>> e2\", \"domain:texture_location2\"]]"
        ).defineList("additionalPowerLoomPirnTextures", ArrayList::new, o -> o instanceof List);

        additionalFleshingMachineTextures = builder.apply("additionalFleshingMachineTextures").comment(
            "If you use custom inputs and outputs in a Fleshing Machine recipe, the registry name of the items and the textures need to be added to this list.",
            "Example: additionalFleshingMachineTextures = [[\"domain:registry_name1\", \"domain:texture_location1\"], [\"domain:registry_name2\", \"domain:texture_location2\"]]"
        ).defineList("additionalFleshingMachineTextures", ArrayList::new, o -> o instanceof List);

        innerBuilder.pop();
    }
}
