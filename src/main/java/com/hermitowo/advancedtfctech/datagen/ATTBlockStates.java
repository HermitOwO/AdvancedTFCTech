package com.hermitowo.advancedtfctech.datagen;

import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.data.models.NongeneratedModels;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import com.google.common.base.Preconditions;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.multiblocks.GristMillMultiblock;
import com.hermitowo.advancedtfctech.common.multiblocks.PowerLoomMultiblock;
import com.hermitowo.advancedtfctech.common.multiblocks.ThresherMultiblock;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTBlockStates extends BlockStateProvider
{
    private final ExistingFileHelper exFileHelper;
    private final NongeneratedModels nongeneratedModels;

    public ATTBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper)
    {
        super(gen, MOD_ID, exFileHelper);
        this.exFileHelper = exFileHelper;
        this.nongeneratedModels = new NongeneratedModels(gen, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        thresher();
        gristMill();
        powerLoom();
    }

    private void thresher()
    {
        ResourceLocation texture = modLoc("multiblock/thresher");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/thresher.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/thresher_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(ATTBlocks.Multiblocks.THRESHER.get(), modelNormal, texture, "", ThresherMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(ATTBlocks.Multiblocks.THRESHER.get(), modelMirrored, texture, "_mirrored", ThresherMultiblock.INSTANCE, true);

        createMultiblock(ATTBlocks.Multiblocks.THRESHER.get(), normal, mirrored, texture);
    }

    private void gristMill()
    {
        ResourceLocation texture = modLoc("multiblock/grist_mill");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/grist_mill.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/grist_mill_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(ATTBlocks.Multiblocks.GRIST_MILL.get(), modelNormal, texture, "", GristMillMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(ATTBlocks.Multiblocks.GRIST_MILL.get(), modelMirrored, texture, "_mirrored", GristMillMultiblock.INSTANCE, true);

        createMultiblock(ATTBlocks.Multiblocks.GRIST_MILL.get(), normal, mirrored, texture);
    }

    private void powerLoom()
    {
        ResourceLocation texture = modLoc("multiblock/power_loom");
        ResourceLocation modelNormal = modLoc("models/multiblock/obj/power_loom.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/obj/power_loom_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(ATTBlocks.Multiblocks.POWER_LOOM.get(), modelNormal, texture, "", PowerLoomMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(ATTBlocks.Multiblocks.POWER_LOOM.get(), modelMirrored, texture, "_mirrored", PowerLoomMultiblock.INSTANCE, true);

        createMultiblock(ATTBlocks.Multiblocks.POWER_LOOM.get(), normal, mirrored, texture);
    }

    private BlockModelBuilder multiblockModel(Block block, ResourceLocation model, ResourceLocation texture, String add, TemplateMultiblock mb, boolean mirror)
    {
        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if(mirror)
        {
            Vec3i size = mb.getSize(null);
            transform = p -> new BlockPos(size.getX() - p.getX() - 1, p.getY(), p.getZ());
        }
        final Vec3i offset = mb.getMasterFromOriginOffset();

        Stream<Vec3i> partsStream = mb.getStructure(null).stream()
            .filter(info -> !info.state.isAir())
            .map(info -> info.pos)
            .map(transform)
            .map(p -> p.subtract(offset));

        String name = getMultiblockPath(block) + add;
        NongeneratedModels.NongeneratedModel base = nongeneratedModels.withExistingParent(name, mcLoc("block"))
            .customLoader(OBJLoaderBuilder::begin).modelLocation(model).detectCullableFaces(false).flipV(true).end()
            .texture("texture", texture)
            .texture("particle", texture);

        BlockModelBuilder split = this.models().withExistingParent(name + "_split", mcLoc("block"))
            .customLoader(SplitModelBuilder::begin)
            .innerModel(base)
            .parts(partsStream.collect(Collectors.toList()))
            .dynamic(false).end();

        return split;
    }

    private String getMultiblockPath(Block b)
    {
        return "multiblock/" + getPath(b);
    }

    private String getPath(Block b)
    {
        return b.getRegistryName().getPath();
    }

    private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel, ResourceLocation particleTexture)
    {
        createMultiblock(b, masterModel, mirroredModel, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED, 180, particleTexture);
    }

    private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel, Property<Boolean> isSlave, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState, int rotationOffset, ResourceLocation particleTex)
    {
        Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
        VariantBlockStateBuilder builder = getVariantBuilder(b);

        boolean[] possibleMirrorStates;
        if(mirroredState != null)
            possibleMirrorStates = new boolean[]{false, true};
        else
            possibleMirrorStates = new boolean[1];
        for(boolean mirrored:possibleMirrorStates)
            for(Direction dir:facing.getPossibleValues())
            {
                final int angleY;
                final int angleX;
                if(facing.getPossibleValues().contains(Direction.UP))
                {
                    angleX = -90 * dir.getStepY();
                    if(dir.getAxis() != Direction.Axis.Y)
                        angleY = getAngle(dir, rotationOffset);
                    else
                        angleY = 0;
                }
                else
                {
                    angleY = getAngle(dir, rotationOffset);
                    angleX = 0;
                }

                ModelFile model = mirrored ? mirroredModel : masterModel;
                VariantBlockStateBuilder.PartialBlockstate partialState = builder.partialState()
//						.with(isSlave, false)
                    .with(facing, dir);

                if(mirroredState != null)
                    partialState = partialState.with(mirroredState, mirrored);

                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
    }

    private int getAngle(Direction dir, int offset)
    {
        return (int) ((dir.toYRot() + offset) % 360);
    }
}
