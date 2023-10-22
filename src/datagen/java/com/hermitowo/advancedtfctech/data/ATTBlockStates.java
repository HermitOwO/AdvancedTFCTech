package com.hermitowo.advancedtfctech.data;

import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.data.models.NongeneratedModels;
import blusunrize.immersiveengineering.data.models.NongeneratedModels.NongeneratedModel;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import com.google.common.collect.ImmutableList;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.blocks.metal.FleshingMachineBlock;
import com.hermitowo.advancedtfctech.common.multiblocks.BeamhouseMultiblock;
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
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
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
        beamhouse();
        fleshingMachine();
    }

    private void thresher()
    {
        ResourceLocation texture = modLoc("multiblock/thresher");
        ResourceLocation modelNormal = modLoc("models/multiblock/thresher.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/thresher_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(ATTBlocks.Multiblocks.THRESHER.get(), modelNormal, texture, "", ThresherMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(ATTBlocks.Multiblocks.THRESHER.get(), modelMirrored, texture, "_mirrored", ThresherMultiblock.INSTANCE, true);

        createMultiblock(ATTBlocks.Multiblocks.THRESHER.get(), normal, mirrored);
    }

    private void gristMill()
    {
        ResourceLocation texture = modLoc("multiblock/grist_mill");
        ResourceLocation modelNormal = modLoc("models/multiblock/grist_mill.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/grist_mill_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(ATTBlocks.Multiblocks.GRIST_MILL.get(), modelNormal, texture, "", GristMillMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(ATTBlocks.Multiblocks.GRIST_MILL.get(), modelMirrored, texture, "_mirrored", GristMillMultiblock.INSTANCE, true);

        createMultiblock(ATTBlocks.Multiblocks.GRIST_MILL.get(), normal, mirrored);
    }

    private void powerLoom()
    {
        ResourceLocation texture = modLoc("multiblock/power_loom");
        ResourceLocation modelNormal = modLoc("models/multiblock/power_loom.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/power_loom_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(ATTBlocks.Multiblocks.POWER_LOOM.get(), modelNormal, texture, "", PowerLoomMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(ATTBlocks.Multiblocks.POWER_LOOM.get(), modelMirrored, texture, "_mirrored", PowerLoomMultiblock.INSTANCE, true);

        createMultiblock(ATTBlocks.Multiblocks.POWER_LOOM.get(), normal, mirrored);
    }

    private void beamhouse()
    {
        ResourceLocation texture = modLoc("multiblock/beamhouse");
        ResourceLocation modelNormal = modLoc("models/multiblock/beamhouse.obj");
        ResourceLocation modelMirrored = modLoc("models/multiblock/beamhouse_mirrored.obj");

        BlockModelBuilder normal = multiblockModel(ATTBlocks.Multiblocks.BEAMHOUSE.get(), modelNormal, texture, "", BeamhouseMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(ATTBlocks.Multiblocks.BEAMHOUSE.get(), modelMirrored, texture, "_mirrored", BeamhouseMultiblock.INSTANCE, true);

        createMultiblock(ATTBlocks.Multiblocks.BEAMHOUSE.get(), normal, mirrored);
    }

    private void fleshingMachine()
    {
        ResourceLocation texture = modLoc("metal_device/fleshing_machine");
        Block block = ATTBlocks.Blocks.FLESHING_MACHINE.get();

        ModelFile model = split(innerObj("metal_device/fleshing_machine.obj", texture), ImmutableList.of(
            FleshingMachineBlockEntity.MASTER_POS, FleshingMachineBlockEntity.DUMMY_POS), texture);

        VariantBlockStateBuilder builder = getVariantBuilder(block);
        for (Direction facing : FleshingMachineBlock.FACING.getPossibleValues())
        {
            int rot = (int) ((facing.toYRot() + 180) % 360);

            builder.partialState()
                .with(FleshingMachineBlock.FACING, facing)
                .setModels(new ConfiguredModel(model, 0, rot, true));
        }
    }

    private ModelFile split(NongeneratedModel model, List<Vec3i> parts, ResourceLocation texture)
    {
        return models().withExistingParent(model.getLocation().getPath() + "_split", mcLoc("block"))
            .customLoader(SplitModelBuilder::begin)
            .innerModel(model)
            .parts(parts)
            .end()
            .texture("particle", texture);
    }

    private NongeneratedModel innerObj(String loc, ResourceLocation texture)
    {
        return obj(loc.substring(0, loc.length() - 4), modLoc(loc), nongeneratedModels, texture);
    }

    private <T extends ModelBuilder<T>> T obj(String name, ResourceLocation model, ModelProvider<T> provider, ResourceLocation texture)
    {
        return provider.withExistingParent(name, mcLoc("block")).customLoader(OBJLoaderBuilder::begin)
            .detectCullableFaces(false)
            .modelLocation(new ResourceLocation(model.getNamespace(), "models/" + model.getPath()))
            .flipV(true)
            .end()
            .texture("texture", texture)
            .texture("particle", texture);
    }

    private BlockModelBuilder multiblockModel(Block block, ResourceLocation model, ResourceLocation texture, String add, TemplateMultiblock mb, boolean mirror)
    {
        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if (mirror)
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

        return this.models().withExistingParent(name + "_split", mcLoc("block"))
            .customLoader(SplitModelBuilder::begin)
            .innerModel(base)
            .parts(partsStream.collect(Collectors.toList()))
            .dynamic(false).end();
    }

    private String getMultiblockPath(Block b)
    {
        return "multiblock/" + getPath(b);
    }

    private String getPath(Block b)
    {
        return Objects.requireNonNull(b.getRegistryName()).getPath();
    }

    private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel)
    {
        VariantBlockStateBuilder builder = getVariantBuilder(b);

        boolean[] possibleMirrorStates;
        possibleMirrorStates = new boolean[] {false, true};
        for (boolean mirrored : possibleMirrorStates)
            for (Direction dir : IEProperties.FACING_HORIZONTAL.getPossibleValues())
            {
                final int angleY;
                final int angleX;
                if (IEProperties.FACING_HORIZONTAL.getPossibleValues().contains(Direction.UP))
                {
                    angleX = -90 * dir.getStepY();
                    if (dir.getAxis() != Direction.Axis.Y)
                        angleY = getAngle(dir);
                    else
                        angleY = 0;
                }
                else
                {
                    angleY = getAngle(dir);
                    angleX = 0;
                }

                ModelFile model = mirrored ? mirroredModel : masterModel;
                VariantBlockStateBuilder.PartialBlockstate partialState = builder.partialState()
//						.with(isSlave, false)
                    .with(IEProperties.FACING_HORIZONTAL, dir);

                partialState = partialState.with(IEProperties.MIRRORED, mirrored);

                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
    }

    private int getAngle(Direction dir)
    {
        return (int) ((dir.toYRot() + 180) % 360);
    }
}
