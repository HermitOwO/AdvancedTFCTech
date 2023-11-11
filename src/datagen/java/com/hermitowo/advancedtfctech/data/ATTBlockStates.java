package com.hermitowo.advancedtfctech.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.data.models.NongeneratedModels;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.multiblocks.ATTMultiblocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ATTBlockStates extends ATTExtendedBlockStateProvider
{
    public final Map<Block, ModelFile> unsplitModels = new HashMap<>();

    public ATTBlockStates(PackOutput output, ExistingFileHelper exFileHelper)
    {
        super(output, exFileHelper);
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "Block States";
    }

    @Override
    protected void registerStatesAndModels()
    {
        createMultiblock(innerObj("block/multiblock/thresher.obj"), ATTMultiblocks.THRESHER);
        createMultiblock(innerObj("block/multiblock/grist_mill.obj"), ATTMultiblocks.GRIST_MILL);
        createMultiblock(innerObj("block/multiblock/power_loom.obj"), ATTMultiblocks.POWER_LOOM);
        createMultiblock(innerObj("block/multiblock/beamhouse.obj"), innerObj("block/multiblock/beamhouse_mirrored.obj"), ATTMultiblocks.BEAMHOUSE);
        createMultiblock(ATTBlocks.FLESHING_MACHINE,
            split(innerObj("block/metal_device/fleshing_machine.obj"), ImmutableList.of(
                FleshingMachineBlockEntity.MASTER_POS, FleshingMachineBlockEntity.DUMMY_POS
            )),
            null, null);
    }

    private void createMultiblock(NongeneratedModels.NongeneratedModel unsplitModel, IETemplateMultiblock multiblock)
    {
        createMultiblock(unsplitModel, multiblock, false);
    }

    private void createMultiblock(NongeneratedModels.NongeneratedModel unsplitModel, NongeneratedModels.NongeneratedModel unsplitMirroredModel, IETemplateMultiblock multiblock)
    {
        createMultiblock(unsplitModel, unsplitMirroredModel, multiblock, false);
    }

    private void createDynamicMultiblock(NongeneratedModels.NongeneratedModel unsplitModel, IETemplateMultiblock multiblock)
    {
        createMultiblock(unsplitModel, multiblock, true);
    }

    private void createMultiblock(NongeneratedModels.NongeneratedModel unsplitModel, IETemplateMultiblock multiblock, boolean dynamic)
    {
        final ModelFile mainModel = split(unsplitModel, multiblock, false, dynamic);
        if (multiblock.getBlock().getStateDefinition().getProperties().contains(IEProperties.MIRRORED))
            createMultiblock(
                multiblock::getBlock,
                mainModel,
                split(mirror(unsplitModel, innerModels), multiblock, true, dynamic),
                IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED
            );
        else
            createMultiblock(multiblock::getBlock, mainModel, null, IEProperties.FACING_HORIZONTAL, null);
    }

    private void createMultiblock(NongeneratedModels.NongeneratedModel unsplitModel, NongeneratedModels.NongeneratedModel unsplitMirroredModel, IETemplateMultiblock multiblock, boolean dynamic)
    {
        final ModelFile mainModel = split(unsplitModel, multiblock, false, dynamic);
        if (multiblock.getBlock().getStateDefinition().getProperties().contains(IEProperties.MIRRORED))
            createMultiblock(
                multiblock::getBlock,
                mainModel,
                split(unsplitMirroredModel, multiblock, true, dynamic),
                IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED
            );
        else
            createMultiblock(multiblock::getBlock, mainModel, null, IEProperties.FACING_HORIZONTAL, null);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel)
    {
        createMultiblock(b, masterModel, null, IEProperties.FACING_HORIZONTAL, null);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel,
                                  @Nullable Property<Boolean> mirroredState)
    {
        createMultiblock(b, masterModel, mirroredModel, IEProperties.FACING_HORIZONTAL, mirroredState);
    }

    private void createMultiblock(Supplier<? extends Block> b, ModelFile masterModel, @Nullable ModelFile mirroredModel,
                                  EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState)
    {
        unsplitModels.put(b.get(), masterModel);
        Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
        VariantBlockStateBuilder builder = getVariantBuilder(b.get());
        boolean[] possibleMirrorStates;
        if (mirroredState != null)
            possibleMirrorStates = new boolean[] {false, true};
        else
            possibleMirrorStates = new boolean[1];
        for (boolean mirrored : possibleMirrorStates)
            for (Direction dir : facing.getPossibleValues())
            {
                final int angleY;
                final int angleX;
                if (facing.getPossibleValues().contains(Direction.UP))
                {
                    angleX = -90 * dir.getStepY();
                    if (dir.getAxis() != Direction.Axis.Y)
                        angleY = getAngle(dir, 180);
                    else
                        angleY = 0;
                }
                else
                {
                    angleY = getAngle(dir, 180);
                    angleX = 0;
                }
                ModelFile model = mirrored ? mirroredModel : masterModel;
                VariantBlockStateBuilder.PartialBlockstate partialState = builder.partialState()
                    .with(facing, dir);
                if (mirroredState != null)
                    partialState = partialState.with(mirroredState, mirrored);
                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
    }
}
