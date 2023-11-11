package com.hermitowo.advancedtfctech.common.multiblocks.shapes;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ThresherShapes implements Function<BlockPos, VoxelShape>
{
    public static final Function<BlockPos, VoxelShape> SHAPE_GETTER = new ThresherShapes();

    private ThresherShapes()
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
            if (bX == 0 && bZ == 1) // Redstone Input Legs
            {
                return Shapes.or(
                    box(0, 0, 0, 16, 8, 16),
                    box(2, 0, 12, 6, 16, 14),
                    box(2, 0, 2, 6, 16, 4)
                );
            }
            else if (!(bX == 1 && bZ == 0))
                return box(0, 0, 0, 16, 8, 16);
        }

        // Second Layer
        if (bY == 1)
        {
            if (bX == 0 && bZ == 0)
                return box(8, 0, 9, 16, 14, 16);
            if (bX == 0 && bZ == 2)
                return box(8, 0, 0, 16, 14, 7);
            if (bX == 2 && bZ == 0)
                return box(0, 0, 9, 8, 14, 16);
            if (bX == 2 && bZ == 2)
                return box(0, 0, 0, 8, 14, 7);

            if (bX == 1)
            {
                if (bZ == 0)
                    return box(0, 0, 9, 16, 14, 16);

                if (bZ == 2)
                {
                    return Shapes.or(
                        box(2, 2, 7, 14, 14, 16),
                        box(0, 0, 0, 16, 14, 7)
                    );
                }
            }
        }

        // Top
        if (bY == 2 && bZ == 1)
        {
            if (bX == 0)
                return box(8, 0, 2, 16, 7, 14);
            if (bX == 2)
                return box(0, 0, 2, 8, 7, 14);
        }

        return Shapes.block();
    }

    // Pixel-based
    private static VoxelShape box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return Shapes.box(minX / 16, minY / 16, minZ / 16, maxX / 16, maxY / 16, maxZ / 16);
    }
}
