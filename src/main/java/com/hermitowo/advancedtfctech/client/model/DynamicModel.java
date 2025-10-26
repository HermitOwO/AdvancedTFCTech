package com.hermitowo.advancedtfctech.client.model;

import java.util.ArrayList;
import java.util.List;
import blusunrize.immersiveengineering.api.ApiUtils;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.data.ModelData;

@EventBusSubscriber(modid = AdvancedTFCTech.MOD_ID, value = Dist.CLIENT)
public class DynamicModel
{
    private static final List<ModelResourceLocation> MODELS = new ArrayList<>();

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional ev)
    {
        for (ModelResourceLocation model : MODELS)
            ev.register(model);
    }

    private final ModelResourceLocation name;

    public DynamicModel(String desc)
    {
        this.name = new ModelResourceLocation(AdvancedTFCTech.rl("dynamic/" + desc), "standalone");
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
        return name.id();
    }
}
