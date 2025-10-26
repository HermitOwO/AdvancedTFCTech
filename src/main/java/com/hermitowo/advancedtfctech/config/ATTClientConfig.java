package com.hermitowo.advancedtfctech.config;

import java.util.List;
import java.util.function.Supplier;

import net.dries007.tfc.config.BaseConfig;

public class ATTClientConfig extends BaseConfig
{
    public final Supplier<List<List<String>>> additionalPowerLoomPirnTextures;
    public final Supplier<List<List<String>>> additionalFleshingMachineTextures;

    ATTClientConfig(ConfigBuilder builder)
    {
        builder.push("compatibility");

        additionalPowerLoomPirnTextures = builder.comment(
            "If you use a custom \"pirn\" in a Power Loom recipe (the second item in input list), the registry name of the pirn item and the textures need to be added to this list.",
            "Example: additionalPowerLoomPirnTextures = [[\"domain:registry_name1\", \"domain:texture_location1\"], [\"domain:registry_namnds List<? extends String>> e2\", \"domain:texture_location2\"]]"
        ).defineListOfList("additionalPowerLoomPirnTextures", List.of(List.of()));

        additionalFleshingMachineTextures = builder.comment(
            "If you use custom inputs and outputs in a Fleshing Machine recipe, the registry name of the items and the textures need to be added to this list.",
            "Example: additionalFleshingMachineTextures = [[\"domain:registry_name1\", \"domain:texture_location1\"], [\"domain:registry_name2\", \"domain:texture_location2\"]]"
        ).defineListOfList("additionalFleshingMachineTextures", List.of(List.of()));

        builder.pop();
    }
}
