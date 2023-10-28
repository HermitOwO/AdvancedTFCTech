package com.hermitowo.advancedtfctech.client.render;

import blusunrize.immersiveengineering.api.client.IVertexBufferHolder;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import com.hermitowo.advancedtfctech.client.model.DynamicModel;
import com.hermitowo.advancedtfctech.common.blockentities.BeamhouseBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

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
        buffer = RenderHelper.mirror(be, poseStack, buffer);

        poseStack.pushPose();

        rotateForFacing(poseStack, be.getFacing());
        RenderHelper.translate(poseStack, 1, 1.75, 0.5);

        poseStack.mulPose(Vector3f.XN.rotationDegrees(angle * 2.5F));
        BARREL_BUFFER.render(RenderType.solid(), combinedLight, combinedOverlay, buffer, poseStack, be.getIsMirrored());

        poseStack.popPose();
    }

    public static void reset()
    {
        BARREL_BUFFER.reset();
    }
}
