package com.hermitowo.advancedtfctech;

import com.hermitowo.advancedtfctech.client.ATTClientEvents;
import com.hermitowo.advancedtfctech.client.ATTClientForgeEvents;
import com.hermitowo.advancedtfctech.client.ATTSounds;
import com.hermitowo.advancedtfctech.common.ATTCreativeTabs;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.capabilities.ATTBlockCapabilities;
import com.hermitowo.advancedtfctech.common.component.ATTComponents;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.multiblocks.ATTMultiblocks;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import com.hermitowo.advancedtfctech.common.network.ATTPacketHandler;
import com.hermitowo.advancedtfctech.common.recipes.ATTRecipeSerializers;
import com.hermitowo.advancedtfctech.common.recipes.ATTRecipeTypes;
import com.hermitowo.advancedtfctech.common.recipes.outputs.ATTItemStackModifiers;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(AdvancedTFCTech.MOD_ID)
public class AdvancedTFCTech
{
    public static final String MOD_ID = "advancedtfctech";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation rl(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public AdvancedTFCTech(ModContainer mod, IEventBus bus)
    {
        mod.registerConfig(ModConfig.Type.CLIENT, ATTConfig.CLIENT.spec());
        mod.registerConfig(ModConfig.Type.SERVER, ATTConfig.SERVER.spec());

        bus.addListener(ATTBlockCapabilities::register);
        bus.addListener(ATTPacketHandler::setup);

        ATTItems.ITEMS.register(bus);
        ATTBlocks.BLOCKS.register(bus);
        ATTBlockEntities.BLOCK_ENTITIES.register(bus);
        ATTContainerTypes.CONTAINERS.register(bus);
        ATTCreativeTabs.CREATIVE_TABS.register(bus);
        ATTRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        ATTRecipeTypes.RECIPE_TYPES.register(bus);
        ATTComponents.COMPONENTS.register(bus);
        ATTSounds.SOUNDS.register(bus);

        ATTItemStackModifiers.TYPES.register(bus);

        ATTForgeEvents.init();
        ATTMultiblocks.init();
        ATTMultiblockLogic.init(bus);

        ATTConfig.SERVER.populateAPI();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ATTClientEvents.init(mod, bus);
            ATTClientForgeEvents.init();
        }
    }
}
