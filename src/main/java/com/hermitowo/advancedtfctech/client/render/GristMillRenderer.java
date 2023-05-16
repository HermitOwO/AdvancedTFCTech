package com.hermitowo.advancedtfctech.client.render;

import java.util.List;
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
    public void render(GristMillBlockEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        if (!te.formed || te.isDummy() || !te.getLevelNonnull().hasChunkAt(te.getBlockPos()))
            return;

        Direction dir = te.getFacing();

        boolean b = te.shouldRenderAsActive();
        float angle = te.animation_driverRotation + (b ? 18 * partialTicks : 0);

        matrixStack.pushPose();

        if (dir == Direction.NORTH)
            matrixStack.translate(1, 1.375, 1);
        if (dir == Direction.EAST)
            matrixStack.translate(0, 1.375, 1);
        if (dir == Direction.SOUTH)
            matrixStack.translate(-1, 1.375, 0);
        if (dir == Direction.WEST)
            matrixStack.translate(1, 1.375, -1);

        matrixStack.translate(te.getFacing().getStepX() * .5, 0, te.getFacing().getStepZ() * .5);

        matrixStack.pushPose();
        matrixStack.mulPose(new Quaternion(new Vector3f(-te.getFacing().getStepZ(), 0, te.getFacing().getStepX()), angle, true));
        renderDriver(DRIVER, matrixStack, bufferIn, dir, combinedLightIn, combinedOverlayIn);
        matrixStack.popPose();

        matrixStack.popPose();
    }

    private void renderDriver(DynamicModel driver, PoseStack matrix, MultiBufferSource buffer, Direction facing, int light, int overlay)
    {
        matrix.pushPose();

        if (facing == Direction.NORTH || facing == Direction.WEST)
            matrix.translate(0, 0, 0);
        if (facing == Direction.EAST)
            matrix.translate(-1, 0, 0);
        if (facing == Direction.SOUTH)
            matrix.translate(0, 0, -1);

        List<BakedQuad> quads = driver.get().getQuads(null, null, Utils.RAND, EmptyModelData.INSTANCE);
        rotateForFacing(matrix, facing);
        RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(RenderType.solid()), matrix, light, overlay);

        matrix.popPose();
    }
}
