package com.hermitowo.advancedtfctech.datagen;

import java.util.Map;
import blusunrize.immersiveengineering.data.blockstates.MultiblockStates;
import com.hermitowo.advancedtfctech.client.render.GristMillRenderer;
import com.hermitowo.advancedtfctech.datagen.DynamicModels.SimpleModelBuilder;
import javax.annotation.Nonnull;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class DynamicModels extends ModelProvider<SimpleModelBuilder>
{
    private final MultiblockStates multiblocks;

    public DynamicModels(MultiblockStates multiblocks, DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, MOD_ID, "dynamic", rl -> new SimpleModelBuilder(rl, existingFileHelper), existingFileHelper);
        this.multiblocks = multiblocks;
    }

    @Override
    protected void registerModels()
    {
        getBuilder(GristMillRenderer.NAME)
            .customLoader(OBJLoaderBuilder::begin)
            .modelLocation(new ResourceLocation(MOD_ID, "models/multiblock/obj/grist_mill_animation.obj"))
            .flipV(true)
            .end();

        for (Map.Entry<Block, ModelFile> multiblock : multiblocks.unsplitModels.entrySet())
            withExistingParent(multiblock.getKey().getRegistryName().getPath(), multiblock.getValue().getLocation());
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "Dynamic models";
    }


    public static class SimpleModelBuilder extends ModelBuilder<SimpleModelBuilder>
    {
        public SimpleModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper)
        {
            super(outputLocation, existingFileHelper);
        }
    }
}
