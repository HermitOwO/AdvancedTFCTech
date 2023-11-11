package com.hermitowo.advancedtfctech.common.multiblocks.shapes;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeamhouseShapes implements Function<BlockPos, VoxelShape>
{
    public static final Function<BlockPos, VoxelShape> SHAPE_GETTER = new BeamhouseShapes();

    private BeamhouseShapes()
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
            if (bX < 3 && bZ < 3)
                return box(0, 0, 0, 16, 8, 16);
            if (bZ == 3)
            {
                if (bX == 1)
                    return Shapes.or(
                        box(0, 0, 0, 16, 8, 16),
                        box(8, 8, 0, 16, 16, 16)
                    );
                if (bX == 3)
                    return Shapes.or(
                        box(0, 0, 0, 16, 8, 16),
                        box(0, 8, 0, 8, 16, 16)
                    );
            }
            if (bX == 3 && bZ == 2)
            {
                return Shapes.or(
                    box(0, 0, 0, 16, 8, 16),
                    box(10, 8, 2, 14, 16, 4),
                    box(10, 8, 12, 14, 16, 14)
                );
            }
        }

        // Second Layer
        if (bY == 1)
        {
            if (bZ == 0)
            {
                if (bX == 0)
                    return box(8, 6, 9, 16, 18, 16);
                if (bX == 1 || bX == 2)
                    return box(0, 6, 9, 16, 18, 16);
            }
            if (bZ == 2)
            {
                if (bX == 0)
                {
                    return Shapes.or(
                        box(8, 6, 0, 16, 18, 7),
                        box(6, 8, 11, 16, 12, 15),
                        box(6, 8, 15, 10, 12, 16)
                    );
                }
                if (bX == 1)
                {
                    return Shapes.or(
                        box(0, 6, 0, 16, 18, 7),
                        box(4, 4, 10, 12, 16, 16),
                        box(0, 8, 11, 4, 12, 15)
                    );
                }
                if (bX == 2)
                    return box(0, 6, 0, 16, 18, 7);
                if (bX == 3)
                    return box(8, 0, 0, 16, 16, 16);
            }
            if (bZ == 1)
            {
                if (bX == 0)
                {
                    return Shapes.or(
                        box(8, 6, 0, 16, 18, 16),
                        box(6, 7, 3, 8, 17, 13)
                    );
                }
                if (bX == 2)
                    return box(0, 6, 0, 16, 18, 16);
            }
            if (bZ == 3 && bX == 2)
                return box(-8, 0, 0, 24, 8, 16);
        }

        // Top Layer
        if (bY == 2)
        {
            if (bZ == 1)
            {
                if (bX == 0)
                    return box(8, 2, 2, 16, 11, 14);
                if (bX == 1 || bX == 2)
                    return box(0, 2, 2, 16, 11, 14);
            }
            else
                return box(0, 0, 0, 0, 0, 0);
        }
        
        return Shapes.block();
    }

    // Pixel-based
    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return Shapes.box(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
    }
}
