package com.hermitowo.advancedtfctech.client;

import java.util.List;
import java.util.function.Supplier;
import com.hermitowo.advancedtfctech.client.model.DynamicModel;
import com.hermitowo.advancedtfctech.client.model.PowerLoomParts;
import com.hermitowo.advancedtfctech.client.render.GristMillRenderer;
import com.hermitowo.advancedtfctech.client.render.PowerLoomRenderer;
import com.hermitowo.advancedtfctech.client.screen.GristMillScreen;
import com.hermitowo.advancedtfctech.client.screen.PowerLoomScreen;
import com.hermitowo.advancedtfctech.client.screen.ThresherScreen;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTClientEvents
{
    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ATTClientEvents::clientSetup);
        bus.addListener(ATTClientEvents::registerModelLoaders);
        bus.addListener(ATTClientEvents::registerLayer);
        bus.addListener(ATTClientEvents::registerRenders);
        bus.addListener(ATTClientEvents::onTextureStitch);
    }

    public static void clientSetup(FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            MenuScreens.register(ATTContainerTypes.THRESHER.getType(), ThresherScreen::new);
            MenuScreens.register(ATTContainerTypes.GRIST_MILL.getType(), GristMillScreen::new);
            MenuScreens.register(ATTContainerTypes.POWER_LOOM.getType(), PowerLoomScreen::new);
        });
    }

    public static void registerModelLoaders(ModelRegistryEvent event)
    {
        GristMillRenderer.DRIVER = new DynamicModel(GristMillRenderer.NAME);
    }

    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(PowerLoomParts.LAYER_LOCATION, PowerLoomParts::createBodyLayer);
    }

    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event)
    {
        registerBERenderNoContext(event, ATTBlockEntities.GRIST_MILL.master(), GristMillRenderer::new);

        event.registerBlockEntityRenderer(ATTBlockEntities.POWER_LOOM.master(), PowerLoomRenderer::new);
    }

    private static <T extends BlockEntity> void registerBERenderNoContext(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render)
    {
        event.registerBlockEntityRenderer(type, $ -> render.get());
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event)
    {
        if (event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
        {
            event.addSprite(new ResourceLocation(MOD_ID, "multiblock/power_loom/burlap"));
            event.addSprite(new ResourceLocation(MOD_ID, "multiblock/power_loom/wool"));
            event.addSprite(new ResourceLocation(MOD_ID, "multiblock/power_loom/pineapple"));

            event.addSprite(new ResourceLocation(MOD_ID, "multiblock/power_loom/fiber_winded_pirn"));
            event.addSprite(new ResourceLocation(MOD_ID, "multiblock/power_loom/wool_winded_pirn"));
            event.addSprite(new ResourceLocation(MOD_ID, "multiblock/power_loom/pineapple_winded_pirn"));

            for (String texture : ATTConfig.CLIENT.additionalPowerLoomClothTextures.get())
                event.addSprite(new ResourceLocation(texture));

            for (List<? extends String> list : ATTConfig.CLIENT.additionalPowerLoomPirnTextures.get())
                event.addSprite(new ResourceLocation(list.get(1)));
        }
    }
}
