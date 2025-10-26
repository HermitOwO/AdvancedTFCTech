package com.hermitowo.advancedtfctech.data.models;

import java.util.Map;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.client.render.BeamhouseRenderer;
import com.hermitowo.advancedtfctech.client.render.GristMillRenderer;
import com.hermitowo.advancedtfctech.data.models.DynamicModels.SimpleModelBuilder;
import com.hermitowo.advancedtfctech.data.blockstates.ATTBlockStates;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DynamicModels extends ModelProvider<SimpleModelBuilder>
{
    private final ATTBlockStates multiblocks;

    public DynamicModels(ATTBlockStates multiblocks, PackOutput output, ExistingFileHelper existingFileHelper)
    {
        super(output, AdvancedTFCTech.MOD_ID, "dynamic", rl -> new SimpleModelBuilder(rl, existingFileHelper), existingFileHelper);
        this.multiblocks = multiblocks;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "Dynamic Models";
    }

    @Override
    protected void registerModels()
    {
        getBuilder("grist_mill_rod")
            .customLoader(ObjModelBuilder::begin)
            .modelLocation(AdvancedTFCTech.rl("models/block/multiblock/grist_mill_rod.obj"))
            .flipV(true)
            .end();

        getBuilder("beamhouse_barrel")
            .customLoader(ObjModelBuilder::begin)
            .modelLocation(AdvancedTFCTech.rl("models/block/multiblock/beamhouse_barrel.obj"))
            .automaticCulling(false)
            .flipV(true)
            .end();

        for (Map.Entry<Block, ModelFile> multiblock : multiblocks.unsplitModels.entrySet())
            withExistingParent(BuiltInRegistries.BLOCK.getKey(multiblock.getKey()).getPath(), multiblock.getValue().getLocation());
    }

    public static class SimpleModelBuilder extends ModelBuilder<SimpleModelBuilder>
    {
        public SimpleModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper)
        {
            super(outputLocation, existingFileHelper);
        }
    }
}
