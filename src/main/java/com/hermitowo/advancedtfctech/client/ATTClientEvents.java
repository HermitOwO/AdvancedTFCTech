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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ATTClientEvents
{
    public static void init(ModContainer mod, IEventBus bus)
    {
        bus.addListener(ATTClientEvents::clientSetup);
        bus.addListener(ATTClientEvents::registerMenuScreens);
        bus.addListener(ATTClientEvents::registerModelLoaders);
        bus.addListener(ATTClientEvents::registerLayer);
        bus.addListener(ATTClientEvents::registerRenders);
    }

    public static void clientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(ATTClientEvents::setupManual);
    }

    public static void registerMenuScreens(RegisterMenuScreensEvent event)
    {
        event.register(ATTContainerTypes.THRESHER.getType(), ThresherScreen::new);
        event.register(ATTContainerTypes.GRIST_MILL.getType(), GristMillScreen::new);
        event.register(ATTContainerTypes.POWER_LOOM.getType(), PowerLoomScreen::new);
        event.register(ATTContainerTypes.BEAMHOUSE.getType(), BeamhouseScreen::new);
        event.register(ATTContainerTypes.FLESHING_MACHINE.getType(), FleshingMachineScreen::new);
    }

    public static void setupManual()
    {
        ManualHelper.addConfigGetter(str -> switch (str)
        {
            case "thresher_operationcost" -> (int) (80 * ATTConfig.SERVER.thresherConfig.energyModifier().getAsDouble());
            case "gristmill_operationcost" -> (int) (80 * ATTConfig.SERVER.gristMillConfig.energyModifier().getAsDouble());
            case "powerloom_operationcost" -> (int) (80 * ATTConfig.SERVER.powerLoomConfig.energyModifier().getAsDouble());
            case "beamhouse_operationcost" -> (int) (20 * ATTConfig.SERVER.beamhouseConfig.energyModifier().getAsDouble());
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
