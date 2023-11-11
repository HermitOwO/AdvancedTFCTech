package com.hermitowo.advancedtfctech.client.render;

import blusunrize.immersiveengineering.api.client.IVertexBufferHolder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import com.hermitowo.advancedtfctech.client.model.DynamicModel;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.GristMillLogic;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;

public class GristMillRenderer extends IEBlockEntityRenderer<MultiblockBlockEntityMaster<GristMillLogic.State>>
{
    public static String NAME = "grist_mill_rod";
    public static DynamicModel DRIVER;
    private static final IVertexBufferHolder DRIVER_BUFFER = IVertexBufferHolder.create(() -> DRIVER.getNullQuads());

    @Override
    public void render(MultiblockBlockEntityMaster<GristMillLogic.State> be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final IMultiblockContext<GristMillLogic.State> ctx = be.getHelper().getContext();
        final GristMillLogic.State state = ctx.getState();
        final MultiblockOrientation orientation = ctx.getLevel().getOrientation();
        Direction facing = orientation.front();

        boolean active = state.shouldRenderAsActive();
        float angle = state.driverAngle + (active ? 18 * partialTicks : 0);
        buffer = RenderHelper.mirror(orientation, poseStack, buffer);

        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        RenderHelper.translate(poseStack, 1, 1.375, 0.5);

        poseStack.mulPose(Axis.XP.rotationDegrees(angle));
        DRIVER_BUFFER.render(RenderType.solid(), combinedLight, combinedOverlay, buffer, poseStack, orientation.mirrored());

        poseStack.popPose();
    }

    public static void reset()
    {
        DRIVER_BUFFER.reset();
    }
}
