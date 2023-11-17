package com.hermitowo.advancedtfctech.client.model;

import java.util.ArrayList;
import java.util.List;
import blusunrize.immersiveengineering.api.ApiUtils;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = AdvancedTFCTech.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class DynamicModel
{
    private static final List<ResourceLocation> MODELS = new ArrayList<>();

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional ev)
    {
        for (ResourceLocation model : MODELS)
            ev.register(model);
    }

    private final ResourceLocation name;

    public DynamicModel(String desc)
    {
        this.name = AdvancedTFCTech.rl("dynamic/" + desc);
        MODELS.add(this.name);
    }

    public BakedModel get()
    {
        final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        return blockRenderer.getBlockModelShaper().getModelManager().getModel(name);
    }

    public List<BakedQuad> getNullQuads()
    {
        return getNullQuads(ModelData.EMPTY);
    }

    public List<BakedQuad> getNullQuads(ModelData data)
    {
        return get().getQuads(null, null, ApiUtils.RANDOM_SOURCE, data, null);
    }

    public ResourceLocation getName()
    {
        return name;
    }
}
