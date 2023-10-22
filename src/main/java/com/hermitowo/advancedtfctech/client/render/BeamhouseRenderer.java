package com.hermitowo.advancedtfctech.client.render;

import blusunrize.immersiveengineering.api.client.IVertexBufferHolder;
import blusunrize.immersiveengineering.client.render.tile.BERenderUtils;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import com.hermitowo.advancedtfctech.client.model.DynamicModel;
import com.hermitowo.advancedtfctech.common.blockentities.BeamhouseBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;

public class BeamhouseRenderer extends IEBlockEntityRenderer<BeamhouseBlockEntity>
{
    public static String NAME = "beamhouse_barrel";
    public static DynamicModel BARREL;
    private static final IVertexBufferHolder BARREL_BUFFER = IVertexBufferHolder.create(() -> BARREL.getNullQuads());

    @Override
    public void render(BeamhouseBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (!be.formed || be.isDummy() || !be.getLevelNonnull().hasChunkAt(be.getBlockPos()))
            return;

        float angle = be.barrelRotation + (be.shouldRenderAsActive() ? 18 * partialTicks : 0);
        final MultiBufferSource bufferMirrored = BERenderUtils.mirror(be, poseStack, buffer);

        poseStack.pushPose();

        translateForFacing(poseStack,be.getFacing(), 1, 1.75, 0.5);
        rotateForFacingMirrored(poseStack, be.getFacing(), be.getIsMirrored());

        poseStack.mulPose(Vector3f.XN.rotationDegrees(angle * 2.5F));

        BARREL_BUFFER.render(RenderType.solid(), combinedLight, combinedOverlay, bufferMirrored, poseStack, be.getIsMirrored());

        poseStack.popPose();
    }

    private static void translateForFacing(PoseStack stack, Direction facing, double x, double y, double z)
    {
        if (facing == Direction.EAST)
            stack.translate(-z, y, x);
        else if (facing == Direction.SOUTH)
            stack.translate(-x, y, -z);
        else if (facing == Direction.WEST)
            stack.translate(z, y, -x);
        else
            stack.translate(x, y, z);
    }

    private static void rotateForFacingMirrored(PoseStack stack, Direction facing, boolean isMirrored)
    {
        if (isMirrored)
        {
            if (facing == Direction.NORTH || facing == Direction.SOUTH)
                stack.translate(-1, 0, 0);
            if (facing == Direction.EAST || facing == Direction.WEST)
                stack.translate(0, 0, -1);
        }

        rotateForFacing(stack, facing);
    }

    public static void reset()
    {
        BARREL_BUFFER.reset();
    }
}
