package com.hermitowo.advancedtfctech.common.multiblocks.shapes;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GristMillShapes implements Function<BlockPos, VoxelShape>
{
    public static final Function<BlockPos, VoxelShape> SHAPE_GETTER = new GristMillShapes();

    private GristMillShapes()
    {
    }

    @Override
    public VoxelShape apply(BlockPos posInMultiblock)
    {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        // Base
        if (bY == 0)
        {
            if (bX == 0 && bZ != 1)
            {
                return box(0, 0, 0, 16, 8, 16);
            }

            if (bZ == 1 && bX == 2)
            {
                return Shapes.or(
                    box(0, 0, 0, 16, 8, 16),
                    box(0, 8, 0, 4, 16, 16)
                );
            }

            if (bZ == 0)
            {
                if (bX == 1)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 8, 16),
                        box(0, 8, 8, 16, 16, 16),
                        box(2, 8, 2, 4, 16, 6),
                        box(12, 8, 2, 14, 16, 6)
                    );
                }
                if (bX == 2)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 8, 16),
                        box(0, 8, 8, 4, 16, 16),
                        box(4, 8, 11.5, 16, 16, 15.5)
                    );
                }
                if (bX == 3)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 8, 16),
                        box(1, 8, 8, 5, 16, 16),
                        box(0, 8, 11.5, 1, 16, 15.5)
                    );
                }
            }

            if (bZ == 2)
            {
                if (bX == 2)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 8, 16),
                        box(0, 8, 0, 4, 16, 8),
                        box(4, 8, .5, 16, 16, 4.5)
                    );
                }
                if (bX == 3)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 8, 16),
                        box(1, 8, 0, 5, 16, 8),
                        box(0, 8, .5, 1, 16, 4.5)
                    );
                }
            }
        }

        //Second Layer
        if (bY == 1)
        {
            if (bZ == 1)
            {
                if (bX == 0)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 4, 16),
                        box(1, 4, .5, 15, 12, 15.5)
                    );
                }
                if (bX == 2)
                {
                    return Shapes.or(
                        box(0, 0, 0, 4, 4, 16),
                        box(0, 5, 7, 16, 7, 9)
                    );
                }
                if (bX == 3)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 4, 16),
                        box(1, 4, .5, 15, 12, 15.5),
                        box(15, 5, 5, 16, 11, 11)
                    );
                }
            }
            if (bZ == 0)
            {
                if (bX == 2)
                {
                    return Shapes.or(
                        box(0, 0, 8, 4, 4, 16),
                        box(4, 0, 11.5, 16, 4, 15.5)
                    );
                }
                if (bX == 3)
                {
                    return Shapes.or(
                        box(1, 0, 8, 5, 4, 16),
                        box(0, 0, 11.5, 1, 4, 15.5)
                    );
                }
            }
            if (bZ == 2)
            {
                if (bX == 1)
                {
                    return box(0, 0, 0, 16, 16, 8);
                }
                if (bX == 2)
                {
                    return Shapes.or(
                        box(0, 0, 0, 4, 4, 8),
                        box(4, 0, .5, 16, 4, 4.5)
                    );
                }
                if (bX == 3)
                {
                    return Shapes.or(
                        box(1, 0, 0, 5, 4, 8),
                        box(0, 0, .5, 1, 4, 4.5)
                    );
                }
            }
        }

        return Shapes.block();
    }

    // Pixel-based
    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return Shapes.box(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
    }
}
