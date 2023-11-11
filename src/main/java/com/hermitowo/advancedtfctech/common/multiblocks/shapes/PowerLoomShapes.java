package com.hermitowo.advancedtfctech.common.multiblocks.shapes;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PowerLoomShapes implements Function<BlockPos, VoxelShape>
{
    public static final Function<BlockPos, VoxelShape> SHAPE_GETTER = new PowerLoomShapes();

    private PowerLoomShapes()
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
            if (bX == 0)
            {
                if (bZ == 1)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 4, 16),
                        box(9, 4, 0, 16, 16, 16),
                        box(0, 4, 1, 8, 16, 5),
                        box(8, 4, 0, 9, 16, 6)
                    );
                }
                if (bZ == 3)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 4, 16),
                        box(9, 4, 0, 16, 16, 16),
                        box(0, 4, 11, 8, 16, 15),
                        box(8, 4, 10, 9, 16, 16)
                    );
                }
                if (bZ == 2)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 4, 16),
                        box(9, 4, 0, 16, 16, 16)
                    );
                }
            }
            if (bX == 2)
            {
                if (bZ == 1)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 4, 16),
                        box(0, 4, 0, 5, 16, 16),
                        box(5.5, 4, 1, 15.5, 16, 5),
                        box(5, 4, 1, 6, 16, 6)
                    );
                }
                if (bZ == 3)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 4, 16),
                        box(0, 4, 0, 5, 16, 16),
                        box(5.5, 4, 11, 15.5, 16, 15),
                        box(5, 4, 10, 6, 16, 16)
                    );
                }
                if (bZ != 4)
                {
                    return Shapes.or(
                        box(0, 0, 0, 16, 4, 16),
                        box(0, 4, 0, 5, 16, 16)
                    );
                }
            }
        }

        // Second Layer
        if (bY == 1)
        {
            if (bX == 0)
            {
                if (bZ == 1)
                {
                    return Shapes.or(
                        box(0, 0, 1, 10, 12, 5),
                        box(8, 0, 0, 16, 4, 6)
                    );
                }
                if (bZ == 3)
                {
                    return Shapes.or(
                        box(0, 0, 11, 10, 12, 15),
                        box(8, 0, 10, 16, 4, 16)
                    );
                }
            }
            if (bX == 1)
            {
                if (bZ == 1)
                {
                    return box(8, 0, 0, 16, 16, 6);
                }
                if (bZ == 2)
                {
                    return Shapes.empty();
                }
                if (bZ == 3)
                {
                    return box(8, 0, 10, 16, 16, 16);
                }
                if (bZ == 4)
                {
                    return box(9.5, 7, 0, 15.5, 13, 10);
                }
            }
            if (bX == 2)
            {
                if (bZ == 1)
                {
                    return Shapes.or(
                        box(6, 0, 1, 15.5, 12, 5),
                        box(0, 0, 0, 6, 16, 6)
                    );
                }
                if (bZ == 3)
                {
                    return Shapes.or(
                        box(6, 0, 11, 15.5, 12, 15),
                        box(0, 0, 10, 6, 16, 16)
                    );
                }
            }
        }

        // Top
        if (bY == 2)
        {
            if (bX == 1)
            {
                if (bZ == 1)
                {
                    return Shapes.or(
                        box(8, 9, 0, 16, 15, 16),
                        box(8, 0, 0, 16, 9.5, 6)
                    );
                }
                if (bZ == 3)
                {
                    return Shapes.or(
                        box(8, 9, 0, 16, 15, 16),
                        box(8, 0, 10, 16, 9.5, 16)
                    );
                }
                else
                {
                    return box(8, 9, 0, 16, 15, 16);
                }
            }
            if (bX == 2)
            {
                if (bZ == 1)
                {
                    return Shapes.or(
                        box(0, 9, 0, 6, 15, 16),
                        box(0, 0, 0, 6, 9.5, 6)
                    );
                }
                if (bZ == 3)
                {
                    return Shapes.or(
                        box(0, 9, 0, 6, 15, 16),
                        box(0, 0, 10, 6, 9.5, 16)
                    );
                }
                else
                {
                    return box(0, 9, 0, 6, 15, 16);
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
