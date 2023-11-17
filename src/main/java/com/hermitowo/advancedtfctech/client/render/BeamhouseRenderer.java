package com.hermitowo.advancedtfctech.client.render;

import blusunrize.immersiveengineering.api.client.IVertexBufferHolder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import com.hermitowo.advancedtfctech.client.model.DynamicModel;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.BeamhouseLogic;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;

public class BeamhouseRenderer extends IEBlockEntityRenderer<MultiblockBlockEntityMaster<BeamhouseLogic.State>>
{
    public static String NAME = "beamhouse_barrel";
    public static DynamicModel BARREL;
    private static final IVertexBufferHolder BARREL_BUFFER = IVertexBufferHolder.create(() -> BARREL.getNullQuads());

    @Override
    public void render(MultiblockBlockEntityMaster<BeamhouseLogic.State> be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final IMultiblockContext<BeamhouseLogic.State> ctx = be.getHelper().getContext();
        final BeamhouseLogic.State state = ctx.getState();
        final MultiblockOrientation orientation = ctx.getLevel().getOrientation();
        Direction facing = orientation.front();

        boolean active = state.shouldRenderAsActive();
        float angle = state.barrelAngle + (active ? 18 * partialTicks : 0);
        buffer = RenderHelper.mirror(orientation, poseStack, buffer);

        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        RenderHelper.translate(poseStack, 0, 0.75, 0.5);

        poseStack.mulPose(Axis.XN.rotationDegrees(angle * 2.5F));
        BARREL_BUFFER.render(RenderType.solid(), combinedLight, combinedOverlay, buffer, poseStack, orientation.mirrored());

        poseStack.popPose();
    }

    public static void reset()
    {
        BARREL_BUFFER.reset();
    }
}
