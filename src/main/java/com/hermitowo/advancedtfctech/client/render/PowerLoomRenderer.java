package com.hermitowo.advancedtfctech.client.render;

import java.util.ArrayList;
import java.util.List;
import blusunrize.immersiveengineering.client.render.tile.BERenderUtils;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import com.hermitowo.advancedtfctech.client.model.PowerLoomParts;
import com.hermitowo.advancedtfctech.common.blockentities.PowerLoomBlockEntity;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class PowerLoomRenderer extends IEBlockEntityRenderer<PowerLoomBlockEntity>
{
    public static final Material material = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(MOD_ID, "multiblock/power_loom"));

    private final PowerLoomParts powerLoomModel;

    public PowerLoomRenderer(BlockEntityRendererProvider.Context context)
    {
        this.powerLoomModel = new PowerLoomParts(context.bakeLayer(PowerLoomParts.LAYER_LOCATION));
    }

    @Override
    public void render(PowerLoomBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        Direction facing = be.getFacing();
        boolean active = be.shouldRenderAsActive();
        float angle = active ? be.getLevel().getGameTime() + partialTicks : 0F;
        final MultiBufferSource bufferMirrored = BERenderUtils.mirror(be, poseStack, buffer);

        poseStack.pushPose();

        poseStack.mulPose(RenderHelpers.rotateDegreesZ(180.0F));
        poseStack.translate(0.0D, -1.5D, 0.0D);

        powerLoomModel.setupAnim(be, partialTicks);
        VertexConsumer vertexConsumer = material.buffer(bufferMirrored, RenderType::entitySolid);
        powerLoomModel.renderToBuffer(poseStack, vertexConsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

        // Rods
        poseStack.pushPose();

        poseStack.translate(-0.6875, 0.625, -0.6875);

        poseStack.mulPose(Vector3f.ZN.rotationDegrees(angle * 1.5F));
        powerLoomModel.rod.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        poseStack.pushPose();

        poseStack.translate(1.6875, 0.625, -0.6875);

        poseStack.mulPose(Vector3f.ZN.rotationDegrees(angle * 1.5F));
        powerLoomModel.rod.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Horizontal Rack
        float rack = be.animation_rack;
        rack = rack * rack * (3.0F - 2.0F * rack);

        poseStack.pushPose();

        poseStack.mulPose(RenderHelpers.rotateDegreesZ(180.0F));
        poseStack.translate(-1.21875 - rack, 0, 1.9375);

        powerLoomModel.rack.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.translate(6.9375, 0, -0.25 + be.animation_rack_side);

        powerLoomModel.rack_side.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // High Rack
        poseStack.pushPose();

        poseStack.mulPose(RenderHelpers.rotateDegreesZ(180.0F));
        poseStack.translate(-1.9375, 0.4375 - be.animation_rack2, 1.9375);

        powerLoomModel.rack2.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Low Rack
        poseStack.pushPose();

        poseStack.mulPose(RenderHelpers.rotateDegreesZ(180.0F));
        poseStack.translate(-2.203125, be.animation_rack2, 1.9375);

        powerLoomModel.rack2.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Holder
        /*List<ItemStack> list = new ArrayList<>(8);
        int amountPirns = 0;
        for (int i = 0; i < 8; i++)
        {
            list.add(be.inventory.get(i));
            amountPirns += be.inventory.get(i).getCount();
        }
        ItemStack stack = list.stream().filter(s -> !s.isEmpty()).findAny().orElse(ItemStack.EMPTY);*/

        /*int amountPirns = 0;
        for (int i = 0; i < be.pirnList().size(); i++)
            amountPirns += be.pirnList().get(i).getCount();*/
        ItemStack stack = be.pirnList().stream().filter(s -> !s.isEmpty()).findAny().orElse(ItemStack.EMPTY);
        int amountPirns = active ? be.amountPirns() - 1 : be.amountPirns();

        poseStack.pushPose();

        poseStack.mulPose(RenderHelpers.rotateDegreesZ(180.0F));
        poseStack.translate(-0.78125, -1.625, 2);

        float tilt = be.finishedCount % 8 * 45.0F;
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(be.animation_pirn + tilt -45.0F));

        powerLoomModel.holder.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Pirns
        for (int i = 0; i < amountPirns; i++)
        {
            poseStack.pushPose();

            poseStack.mulPose(RenderHelpers.rotateDegreesZ(180.0F));
            poseStack.translate(-0.78125, -1.625, 2);

            poseStack.mulPose(Vector3f.ZP.rotationDegrees(be.animation_pirn));

            poseStack.mulPose(Vector3f.ZN.rotationDegrees(45.0F * (i + 1)));

            if (stack.is(ATTItems.FIBER_WINDED_PIRN.get()))
                powerLoomModel.fiber_pirn.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);
            else
                powerLoomModel.wool_pirn.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

            poseStack.popPose();
        }

        if (active)
        {
            poseStack.pushPose();

            poseStack.mulPose(RenderHelpers.rotateDegreesZ(180.0F));
            poseStack.translate(-0.78125, -1.625, 2);

            poseStack.mulPose(Vector3f.ZP.rotationDegrees(be.animation_pirn));

            poseStack.mulPose(Vector3f.ZN.rotationDegrees(45.0F));
            poseStack.translate(be.animation_pirn_x - rack + 0.72625, be.animation_pirn_y, -be.animation_pirn_z);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(45.0F));

            if (stack.is(ATTItems.FIBER_WINDED_PIRN.get()))
                powerLoomModel.fiber_pirn.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);
            else
                powerLoomModel.wool_pirn.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

            poseStack.popPose();
        }
    }
}
