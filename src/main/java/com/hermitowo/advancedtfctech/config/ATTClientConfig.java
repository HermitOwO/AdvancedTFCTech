package com.hermitowo.advancedtfctech.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTClientConfig
{
    public final ConfigValue<List<? extends String>> additionalPowerLoomClothTextures;
    public final ConfigValue<List<? extends List<? extends String>>> additionalPowerLoomPirnTextures;

    ATTClientConfig(Builder innerBuilder)
    {
        Function<String, Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.client." + name);

        innerBuilder.push("compatibility");

        additionalPowerLoomClothTextures = builder.apply("additionalPowerLoomClothTextures").comment(
            "If you use a custom \"in_progress_texture\" in a Power Loom recipe, the textures need to be added to this list.",
            "Note that the custom textures cannot be located in a datapack because datapacks are loaded after all the textures are stitched.",
            "Instead, the textures need to be located in a resourcepack.",
            "Example: additionalPowerLoomClothTextures = [\"domain:texture_location1\", \"domain:texture_location2\"]"
        ).defineList("additionalPowerLoomClothTextures", ArrayList::new, o -> o instanceof String s && ResourceLocation.isValidResourceLocation(s));

        additionalPowerLoomPirnTextures = builder.apply("additionalPowerLoomPirnTextures").comment(
            "If you use a custom \"pirn\" in a Power Loom recipe (the second item in input list), the registry name of the pirn item and the textures need to be added to this list.",
            "Note that the custom textures cannot be located in a datapack because datapacks are loaded after all the textures are stitched.",
            "Instead, the textures need to be located in a resourcepack.",
            "Example: additionalPowerLoomPirnTextures = [[\"domain:registry_name1\", \"domain:texture_location1\"], [\"domain:registry_name2\", \"domain:texture_location2\"]]"
        ).defineList("additionalPowerLoomPirnTextures", ArrayList::new, o -> o instanceof List);

        innerBuilder.pop();
    }
}
