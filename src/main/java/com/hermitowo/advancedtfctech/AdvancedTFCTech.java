package com.hermitowo.advancedtfctech;

import com.hermitowo.advancedtfctech.client.ATTClientEvents;
import com.hermitowo.advancedtfctech.client.ATTClientForgeEvents;
import com.hermitowo.advancedtfctech.client.ATTSounds;
import com.hermitowo.advancedtfctech.common.ATTCreativeTabs;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.multiblocks.ATTMultiblocks;
import com.hermitowo.advancedtfctech.common.network.ATTPacketHandler;
import com.hermitowo.advancedtfctech.common.recipes.ATTRecipeSerializers;
import com.hermitowo.advancedtfctech.common.recipes.ATTRecipeTypes;
import com.hermitowo.advancedtfctech.common.recipes.outputs.ATTItemStackModifiers;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(AdvancedTFCTech.MOD_ID)
public class AdvancedTFCTech
{
    public static final String MOD_ID = "advancedtfctech";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation rl(String path)
    {
        return new ResourceLocation(MOD_ID, path);
    }

    public AdvancedTFCTech()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);

        ATTItems.ITEMS.register(bus);
        ATTBlocks.BLOCKS.register(bus);
        ATTBlockEntities.BLOCK_ENTITIES.register(bus);
        ATTContainerTypes.CONTAINERS.register(bus);
        ATTCreativeTabs.CREATIVE_TABS.register(bus);
        ATTRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        ATTRecipeTypes.RECIPE_TYPES.register(bus);
        ATTSounds.SOUNDS.register(bus);

        ATTConfig.init();
        ATTPacketHandler.init();
        ATTForgeEvents.init();
        ATTMultiblocks.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ATTClientEvents.init();
            ATTClientForgeEvents.init();
        }
    }

    private void setup(FMLCommonSetupEvent event)
    {
        ATTItemStackModifiers.registerItemStackModifierTypes();
    }
}
