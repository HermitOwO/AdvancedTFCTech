package com.hermitowo.advancedtfctech;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import com.hermitowo.advancedtfctech.client.ATTSounds;
import com.hermitowo.advancedtfctech.common.recipes.ATTRecipeTypes;
import com.hermitowo.advancedtfctech.client.ATTClientEvents;
import com.hermitowo.advancedtfctech.client.ATTClientForgeEvents;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.recipes.ATTRecipeSerializers;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.multiblocks.BeamhouseMultiblock;
import com.hermitowo.advancedtfctech.common.multiblocks.GristMillMultiblock;
import com.hermitowo.advancedtfctech.common.multiblocks.PowerLoomMultiblock;
import com.hermitowo.advancedtfctech.common.multiblocks.ThresherMultiblock;
import com.hermitowo.advancedtfctech.common.recipes.outputs.ATTItemStackModifiers;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(AdvancedTFCTech.MOD_ID)
public class AdvancedTFCTech
{
    public static final String MOD_ID = "advancedtfctech";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AdvancedTFCTech()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::loadComplete);

        ATTItems.ITEMS.register(bus);
        ATTBlocks.BLOCKS.register(bus);
        ATTBlockEntities.BLOCK_ENTITIES.register(bus);
        ATTContainerTypes.CONTAINERS.register(bus);
        ATTRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        ATTRecipeTypes.RECIPE_TYPES.register(bus);
        ATTSounds.SOUNDS.register(bus);

        ATTConfig.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ATTClientEvents.init();
            ATTClientForgeEvents.init();
        }
    }

    private void setup(FMLCommonSetupEvent event)
    {
        ATTItemStackModifiers.registerItemStackModifierTypes();

        MultiblockHandler.registerMultiblock(ThresherMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(GristMillMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(PowerLoomMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(BeamhouseMultiblock.INSTANCE);
    }

    private void loadComplete(FMLLoadCompleteEvent event)
    {
        event.enqueueWork(() -> ManualHelper.addConfigGetter(str -> switch (str)
            {
                case "thresher_operationcost" -> (int) (80 * ATTConfig.SERVER.thresher_energyModifier.get());
                case "gristmill_operationcost" -> (int) (80 * ATTConfig.SERVER.gristMill_energyModifier.get());
                case "powerloom_operationcost" -> (int) (80 * ATTConfig.SERVER.powerLoom_energyModifier.get());
                case "beamhouse_operationcost" -> (int) (20 * ATTConfig.SERVER.beamhouse_energyModifier.get());
                default -> -1;
            }));
    }
}
