package com.hermitowo.advancedtfctech;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;

import com.hermitowo.advancedtfctech.api.crafting.ATTRecipeTypes;
import com.hermitowo.advancedtfctech.client.ClientProxy;
import com.hermitowo.advancedtfctech.common.CommonProxy;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.multiblocks.GristMillMultiblock;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.crafting.ATTSerializers;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.multiblocks.PowerLoomMultiblock;
import com.hermitowo.advancedtfctech.common.multiblocks.ThresherMultiblock;
import com.hermitowo.advancedtfctech.config.ATTServerConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
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
        ATTSerializers.RECIPE_SERIALIZERS.register(bus);
        ATTRecipeTypes.RECIPE_TYPES.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ATTServerConfig.ALL);
    }

    private void setup(FMLCommonSetupEvent event)
    {
        MultiblockHandler.registerMultiblock(ThresherMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(GristMillMultiblock.INSTANCE);
        MultiblockHandler.registerMultiblock(PowerLoomMultiblock.INSTANCE);
    }

    public static <T> Supplier<T> bootstrapErrorToXCPInDev(Supplier<T> in)
    {
        if(FMLLoader.isProduction())
            return in;
        return () -> {
            try
            {
                return in.get();
            }
            catch(BootstrapMethodError e)
            {
                throw new RuntimeException(e);
            }
        };
    }

    public void loadComplete(FMLLoadCompleteEvent event)
    {
        proxy.completed(event);
    }

    public static final CommonProxy proxy = DistExecutor.safeRunForDist(bootstrapErrorToXCPInDev(() -> ClientProxy::new), bootstrapErrorToXCPInDev(() -> CommonProxy::new));

    public static ResourceLocation rl(String path)
    {
        return new ResourceLocation(MOD_ID, path);
    }
}
