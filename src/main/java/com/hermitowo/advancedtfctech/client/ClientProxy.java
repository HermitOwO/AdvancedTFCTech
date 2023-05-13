package com.hermitowo.advancedtfctech.client;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.ManualHelper;
import com.electronwill.nightconfig.core.Config;
import com.hermitowo.advancedtfctech.client.render.DynamicModel;
import com.hermitowo.advancedtfctech.client.render.GristMillRenderer;
import com.hermitowo.advancedtfctech.client.screen.GristMillScreen;
import com.hermitowo.advancedtfctech.client.screen.ThresherScreen;
import com.hermitowo.advancedtfctech.client.utils.MCUtil;
import com.hermitowo.advancedtfctech.common.CommonProxy;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.config.ATTServerConfig;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID, bus = Bus.MOD)
public class ClientProxy extends CommonProxy
{
    public static void registerContainersAndScreens()
    {
        MenuScreens.register(ATTContainerTypes.THRESHER.getType(), ThresherScreen::new);
        MenuScreens.register(ATTContainerTypes.GRIST_MILL.getType(), GristMillScreen::new);
    }

    @Override
    public void completed(ParallelDispatchEvent event)
    {
        event.enqueueWork(() -> ManualHelper.addConfigGetter(str -> switch(str)
            {
                case "thresher_operationcost" -> (int) (80 * ATTServerConfig.GENERAL.thresher_energyModifier.get());
                case "gristmill_operationcost" -> (int) (80 * ATTServerConfig.GENERAL.gristMill_energyModifier.get());
                default -> {
                    // Last resort
                    Config cfg = ATTServerConfig.getRawConfig();
                    if(cfg.contains(str)){
                        yield cfg.get(str);
                    }
                    yield null;
                }
            }));
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelRegistryEvent event)
    {
        GristMillRenderer.DRIVER = new DynamicModel(GristMillRenderer.NAME);
    }

    @SubscribeEvent
    public static void registerRenders(RegisterRenderers event)
    {
        registerBERenders(event);
    }

    private static <T extends BlockEntity>
    void registerBERenderNoContext(RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render)
    {
        event.registerBlockEntityRenderer(type, $ -> render.get());
    }

    public static void registerBERenders(RegisterRenderers event)
    {
        registerBERenderNoContext(event, ATTBlockEntities.GRIST_MILL.master(), GristMillRenderer::new);
    }
    @Override
    public Level getClientWorld()
    {
        return MCUtil.getLevel();
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event)
    {
        registerContainersAndScreens();
    }
}
