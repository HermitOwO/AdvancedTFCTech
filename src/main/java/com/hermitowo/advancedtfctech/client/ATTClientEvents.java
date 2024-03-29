package com.hermitowo.advancedtfctech.client;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.ManualHelper;
import com.hermitowo.advancedtfctech.client.model.DynamicModel;
import com.hermitowo.advancedtfctech.client.model.PowerLoomParts;
import com.hermitowo.advancedtfctech.client.render.BeamhouseRenderer;
import com.hermitowo.advancedtfctech.client.render.FleshingMachineRenderer;
import com.hermitowo.advancedtfctech.client.render.GristMillRenderer;
import com.hermitowo.advancedtfctech.client.render.PowerLoomRenderer;
import com.hermitowo.advancedtfctech.client.screen.BeamhouseScreen;
import com.hermitowo.advancedtfctech.client.screen.FleshingMachineScreen;
import com.hermitowo.advancedtfctech.client.screen.GristMillScreen;
import com.hermitowo.advancedtfctech.client.screen.PowerLoomScreen;
import com.hermitowo.advancedtfctech.client.screen.ThresherScreen;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ATTClientEvents
{
    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ATTClientEvents::clientSetup);
        bus.addListener(ATTClientEvents::registerModelLoaders);
        bus.addListener(ATTClientEvents::registerLayer);
        bus.addListener(ATTClientEvents::registerRenders);
    }

    public static void clientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            MenuScreens.register(ATTContainerTypes.THRESHER.getType(), ThresherScreen::new);
            MenuScreens.register(ATTContainerTypes.GRIST_MILL.getType(), GristMillScreen::new);
            MenuScreens.register(ATTContainerTypes.POWER_LOOM.getType(), PowerLoomScreen::new);
            MenuScreens.register(ATTContainerTypes.BEAMHOUSE.getType(), BeamhouseScreen::new);
            MenuScreens.register(ATTContainerTypes.FLESHING_MACHINE.getType(), FleshingMachineScreen::new);

            setupManual();
        });
    }

    public static void setupManual()
    {
        ManualHelper.addConfigGetter(str -> switch (str)
        {
            case "thresher_operationcost" -> (int) (80 * ATTConfig.SERVER.thresherConfig.energyModifier().get());
            case "gristmill_operationcost" -> (int) (80 * ATTConfig.SERVER.gristMillConfig.energyModifier().get());
            case "powerloom_operationcost" -> (int) (80 * ATTConfig.SERVER.powerLoomConfig.energyModifier().get());
            case "beamhouse_operationcost" -> (int) (20 * ATTConfig.SERVER.beamhouseConfig.energyModifier().get());
            default -> -1;
        });
    }

    public static void registerModelLoaders(ModelEvent.RegisterGeometryLoaders event)
    {
        GristMillRenderer.DRIVER = new DynamicModel(GristMillRenderer.NAME);
        BeamhouseRenderer.BARREL = new DynamicModel(BeamhouseRenderer.NAME);

        ATTClientMultiblockProperties.initModels();
    }

    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(PowerLoomParts.LAYER_LOCATION, PowerLoomParts::createBodyLayer);
    }

    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event)
    {
        registerBERenderNoContext(event, ATTMultiblockLogic.GRIST_MILL.masterBE().get(), GristMillRenderer::new);
        registerBERenderNoContext(event, ATTMultiblockLogic.BEAMHOUSE.masterBE().get(), BeamhouseRenderer::new);
        registerBERenderNoContext(event, ATTBlockEntities.FLESHING_MACHINE.master(), FleshingMachineRenderer::new);

        event.registerBlockEntityRenderer(ATTMultiblockLogic.POWER_LOOM.masterBE().get(), PowerLoomRenderer::new);
    }

    private static <T extends BlockEntity> void registerBERenderNoContext(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render)
    {
        event.registerBlockEntityRenderer(type, $ -> render.get());
    }

    static
    {
        IEApi.renderCacheClearers.add(GristMillRenderer::reset);
        IEApi.renderCacheClearers.add(BeamhouseRenderer::reset);
        IEApi.renderCacheClearers.add(FleshingMachineRenderer::reset);
    }
}
