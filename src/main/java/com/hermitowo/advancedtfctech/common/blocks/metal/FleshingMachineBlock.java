package com.hermitowo.advancedtfctech.common.blocks.metal;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEEntityBlock;
import blusunrize.immersiveengineering.common.blocks.PlacementLimitation;
import com.google.common.collect.ImmutableList;
import com.hermitowo.advancedtfctech.common.blockentities.ATTBlockEntities;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FleshingMachineBlock extends IEEntityBlock<FleshingMachineBlockEntity>
{
    public static final Property<Direction> FACING = IEProperties.FACING_HORIZONTAL;
    public static final Property<Boolean> DUMMY = IEProperties.MULTIBLOCKSLAVE;

    public FleshingMachineBlock()
    {
        super(ATTBlockEntities.FLESHING_MACHINE, Properties.of(Material.METAL)
            .sound(SoundType.METAL)
            .strength(3, 15)
            .requiresCorrectToolForDrops()
            .isViewBlocking((state, blockReader, pos) -> false)
            .noOcclusion());
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(DUMMY, FACING, IEProperties.ACTIVE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot)
    {
        Direction newFacing = rot.rotate(state.getValue(FACING));
        return state.setValue(FACING, newFacing);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
            return state;
        Direction oldFacing = state.getValue(FACING);
        Direction newFacing = mirror.mirror(oldFacing);
        boolean oldDummy = state.getValue(DUMMY);
        boolean newDummy = !oldDummy;
        return state.setValue(FACING, newFacing).setValue(DUMMY, newDummy);
    }

    @Override
    public boolean canIEBlockBePlaced(BlockState newState, BlockPlaceContext context)
    {
        BlockPos start = context.getClickedPos();
        Direction facing = PlacementLimitation.HORIZONTAL.getDirectionForPlacement(context);
        Direction dummyDir = FleshingMachineBlock.getDummyOffset(context.getLevel(), context.getClickedPos(), facing, context);
        return areAllReplaceable(start, start.relative(dummyDir), context);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction updateSide, BlockState updatedState, LevelAccessor world, BlockPos currentPos, BlockPos updatedPos)
    {
        Direction facing = state.getValue(FACING);
        boolean dummy = state.getValue(DUMMY);
        {
            // Check if current facing is correct
            BlockPos otherHalf = currentPos.relative(facing.getClockWise(), dummy ? -1 : 1);
            BlockState otherState = world.getBlockState(otherHalf);
            if (otherState.getBlock() == this && otherState.getValue(FACING) == facing && otherState.getValue(DUMMY) == !dummy)
                return state;
        }
        // Find correct facing, or remove
        for (Direction candidate : FACING.getPossibleValues())
            if (candidate != facing)
            {
                BlockPos otherHalf = currentPos.relative(candidate.getClockWise(), dummy ? -1 : 1);
                BlockState otherState = world.getBlockState(otherHalf);
                if (otherState.getBlock() == this && otherState.getValue(FACING) == candidate && otherState.getValue(DUMMY) == !dummy)
                    return state.setValue(FACING, candidate);
            }
        return Blocks.AIR.defaultBlockState();
    }

    public static Direction getDummyOffset(Level world, BlockPos pos, Direction facing, BlockPlaceContext context)
    {
        Direction dummyDir;
        if (facing.getAxis() == Direction.Axis.X)
            dummyDir = context.getClickLocation().z < .5 ? Direction.NORTH : Direction.SOUTH;
        else
            dummyDir = context.getClickLocation().x < .5 ? Direction.WEST : Direction.EAST;
        BlockPos dummyPos = pos.relative(dummyDir);
        if (!world.getBlockState(dummyPos).canBeReplaced(BlockPlaceContext.at(context, dummyPos, dummyDir)))
            dummyDir = dummyDir.getOpposite();
        return dummyDir;
    }

    public static void placeDummies(BlockState state, Level world, BlockPos pos, BlockPlaceContext context)
    {
        Direction facing = state.getValue(FACING);
        Direction dummyDir = FleshingMachineBlock.getDummyOffset(world, pos, facing, context);
        BlockPos dummyPos = pos.relative(dummyDir);
        boolean mirror = dummyDir != facing.getClockWise();
        if (mirror)
            world.setBlockAndUpdate(pos, state.setValue(DUMMY, true));
        world.setBlockAndUpdate(dummyPos, state.setValue(DUMMY, !mirror));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(DUMMY), state.getValue(FACING));
    }

    private static final CachedShapesWithTransform<Boolean, Direction> SHAPES = CachedShapesWithTransform.createDirectional(
        dummy -> {
            if (dummy)
                return ImmutableList.of(
                    pixel(15, 5, 5, 16, 11, 11),
                    pixel(9, 0, 0, 15, 12, 16),
                    pixel(9, 12, 0, 15, 16, 12),
                    pixel(0, 14, 0, 9, 16, 10),
                    pixel(0, 5, 0, 9, 6, 16),
                    pixel(0, 1, 11, 9, 3, 14),
                    pixel(0, 6, 0, 9, 14, 2)
                );
            else
                return ImmutableList.of(
                    pixel(0, 0, 0, 4, 12, 16),
                    pixel(0, 12, 0, 4, 16, 12),
                    pixel(4, 14, 0, 16, 16, 10),
                    pixel(4, 5, 0, 16, 6, 16),
                    pixel(4, 1, 11, 16, 3, 14),
                    pixel(4, 6, 0, 16, 14, 2)
                );
        }
    );

    // Pixel-based
    private static AABB pixel(double x0, double y0, double z0, double x1, double y1, double z1)
    {
        return new AABB(x0 / 16D, y0 / 16D, z0 / 16D, x1 / 16D, y1 / 16D, z1 / 16D);
    }
}
