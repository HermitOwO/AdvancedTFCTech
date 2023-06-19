package com.hermitowo.advancedtfctech.client.render;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.client.IVertexBufferHolder;
import blusunrize.immersiveengineering.client.render.tile.BERenderUtils;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import blusunrize.immersiveengineering.common.util.Utils;
import com.hermitowo.advancedtfctech.client.model.DynamicModel;
import com.hermitowo.advancedtfctech.common.blockentities.GristMillBlockEntity;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

public class GristMillRenderer extends IEBlockEntityRenderer<GristMillBlockEntity>
{
    public static String NAME = "grist_mill_animation";
    public static DynamicModel DRIVER;

    private static final IVertexBufferHolder MODEL_BUFFER = IVertexBufferHolder.create(() -> {
        BlockState state = ATTBlocks.Multiblocks.GRIST_MILL.get().defaultBlockState()
            .setValue(IEProperties.FACING_HORIZONTAL, Direction.NORTH);
        return DRIVER.get().getQuads(state, null, Utils.RAND, EmptyModelData.INSTANCE);
    });

    @Override
    public void render(GristMillBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (!be.formed || be.isDummy() || !be.getLevelNonnull().hasChunkAt(be.getBlockPos()))
            return;

        float angle = be.animation_driverRotation + (be.shouldRenderAsActive() ? 18 * partialTicks : 0);
        final MultiBufferSource bufferMirrored = BERenderUtils.mirror(be, poseStack, buffer);

        poseStack.pushPose();

        translateForFacing(poseStack, be.getFacing(), 1, 1.375, 0.5);
        rotateForFacingMirrored(poseStack, be.getFacing(), be.getIsMirrored());

        poseStack.mulPose(Vector3f.XP.rotationDegrees(angle));

        MODEL_BUFFER.render(RenderType.solid(), combinedLight, combinedOverlay, bufferMirrored, poseStack);

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
}
