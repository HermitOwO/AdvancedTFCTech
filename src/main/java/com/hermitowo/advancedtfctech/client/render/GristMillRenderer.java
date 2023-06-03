package com.hermitowo.advancedtfctech.client.render;

import java.util.List;
import blusunrize.immersiveengineering.client.render.tile.BERenderUtils;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import com.hermitowo.advancedtfctech.common.blockentities.GristMillBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;

public class GristMillRenderer extends IEBlockEntityRenderer<GristMillBlockEntity>
{
    public static String NAME = "grist_mill_animation";
    public static DynamicModel DRIVER;

    @Override
    public void render(GristMillBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (!be.formed || be.isDummy() || !be.getLevelNonnull().hasChunkAt(be.getBlockPos()))
            return;

        Direction dir = be.getFacing();

        boolean b = be.shouldRenderAsActive();
        float angle = be.animation_driverRotation + (b ? 18 * partialTicks : 0);
        final MultiBufferSource bufferMirrored = BERenderUtils.mirror(be, poseStack, buffer);

        poseStack.pushPose();

        int i = be.getIsMirrored() ? -1 : 0;

        if (dir == Direction.NORTH)
            poseStack.translate(1 + i, 1.375, 1);
        if (dir == Direction.EAST)
            poseStack.translate(0, 1.375, 1 + i);
        if (dir == Direction.SOUTH)
            poseStack.translate(-1 + i, 1.375, 0);
        if (dir == Direction.WEST)
            poseStack.translate(1, 1.375, -1 + i);

        poseStack.translate(be.getFacing().getStepX() * .5, 0, be.getFacing().getStepZ() * .5);

        poseStack.mulPose(new Quaternion(new Vector3f(-be.getFacing().getStepZ(), 0, be.getFacing().getStepX()), angle, true));
        renderDriver(DRIVER, poseStack, bufferMirrored, dir, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private void renderDriver(DynamicModel driver, PoseStack poseStack, MultiBufferSource buffer, Direction facing, int light, int overlay)
    {
        poseStack.pushPose();

        if (facing == Direction.NORTH || facing == Direction.WEST)
            poseStack.translate(0, 0, 0);
        if (facing == Direction.EAST)
            poseStack.translate(-1, 0, 0);
        if (facing == Direction.SOUTH)
            poseStack.translate(0, 0, -1);

        List<BakedQuad> quads = driver.get().getQuads(null, null, Utils.RAND, EmptyModelData.INSTANCE);
        rotateForFacing(poseStack, facing);
        RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(RenderType.solid()), poseStack, light, overlay);

        poseStack.popPose();
    }
}
