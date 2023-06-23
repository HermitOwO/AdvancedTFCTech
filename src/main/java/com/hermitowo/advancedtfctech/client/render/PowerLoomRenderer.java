package com.hermitowo.advancedtfctech.client.render;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.render.tile.BERenderUtils;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import com.hermitowo.advancedtfctech.api.crafting.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.client.model.PowerLoomParts;
import com.hermitowo.advancedtfctech.common.blockentities.PowerLoomBlockEntity;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
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

        boolean isMirrored = be.getIsMirrored();
        boolean active = !be.processQueue.isEmpty();
        float angle = be.animation_rodRotation + (be.shouldRenderAsActive() ? 1.75F * partialTicks : 0);
        final MultiBufferSource bufferMirrored = BERenderUtils.mirror(be, poseStack, buffer);
        VertexConsumer vertexConsumer = material.buffer(bufferMirrored, RenderType::entitySolid);

        // Output Rod
        poseStack.pushPose();

        translateForFacing(poseStack, facing, -0.6875, 0.625, -0.6875);
        rotateForFacingMirroredInverted(poseStack, facing, isMirrored);

        poseStack.mulPose(Vector3f.ZN.rotationDegrees(angle));
        powerLoomModel.rod.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Input Rod
        poseStack.pushPose();

        translateForFacing(poseStack, facing, 1.6875, 0.625, -0.6875);
        rotateForFacingMirroredInverted(poseStack, facing, isMirrored);

        poseStack.mulPose(Vector3f.ZN.rotationDegrees(angle));
        powerLoomModel.rod.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Horizontal Rack
        float rack = be.animation_rack;
        rack = rack * rack * (3.0F - 2.0F * rack);

        poseStack.pushPose();

        rotateRackForFacing(poseStack, facing);

        if (facing == Direction.NORTH)
            poseStack.translate(-1.211 - rack, 0, 1.9375);
        if (facing == Direction.EAST)
            poseStack.translate(-1.9375, 0, -1.211 - rack);
        if (facing == Direction.SOUTH)
            poseStack.translate(-0.789 + rack, 0, -1.9375);
        if (facing == Direction.WEST)
            poseStack.translate(1.9375, 0, -0.789 + rack);

        rotateForFacingMirrored(poseStack, facing, isMirrored);

        powerLoomModel.rack.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.translate(6.9375, 0, -0.25 + be.animation_rack_side);

        powerLoomModel.rack_side.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // High Rack
        poseStack.pushPose();

        rotateAndTranslate(poseStack, facing, -1.9375, 0.4375 - be.animation_rack2, 1.9375);
        rotateForFacingMirrored(poseStack, facing, isMirrored);

        powerLoomModel.rack2.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Low Rack
        poseStack.pushPose();

        rotateAndTranslate(poseStack, facing, -1.9375, -0.4375 + be.animation_rack2, 1.9375);
        rotateForFacingMirrored(poseStack, facing, isMirrored);

        powerLoomModel.rack3.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Holder
        poseStack.pushPose();

        rotateAndTranslate(poseStack, facing, -0.78125, -1.625, 2);
        rotateForFacingMirrored(poseStack, facing, isMirrored);

        float tilt = be.holderRotation * 45.0F;
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(be.animation_pirn + tilt));

        powerLoomModel.holder.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        /*
         *
         */

        VertexConsumer consumer = bufferMirrored.getBuffer(RenderType.solid());
        TextureAtlas blockMap = ClientUtils.mc().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);

        ItemStack pirn = be.pirnList.stream().filter(s -> !s.isEmpty()).findAny().orElse(ItemStack.EMPTY);
        int amountPirns = 0;
        for (int i = 0; i < 8; i++)
            amountPirns += be.inventory.get(i).getCount();
        amountPirns = active ? amountPirns - 1 : amountPirns;

        Map<String, String> pirnTextures = new HashMap<>();
        pirnTextures.put("advancedtfctech:fiber_winded_pirn", "advancedtfctech:multiblock/power_loom/fiber_winded_pirn");
        pirnTextures.put("advancedtfctech:silk_winded_pirn", "advancedtfctech:multiblock/power_loom/wool_winded_pirn");
        pirnTextures.put("advancedtfctech:wool_winded_pirn", "advancedtfctech:multiblock/power_loom/wool_winded_pirn");
        pirnTextures.put("advancedtfctech:pineapple_winded_pirn", "advancedtfctech:multiblock/power_loom/pineapple_winded_pirn");

        Map<String, String> configTextures =
            ATTConfig.CLIENT.additionalPowerLoomPirnTextures.get().stream().collect(Collectors.toMap(list -> list.get(0), list -> list.get(1)));

        pirnTextures.putAll(configTextures);

        TextureAtlasSprite pirnTexture = blockMap.getSprite(new ResourceLocation(
            pirnTextures.entrySet().stream().filter(entry -> entry.getKey().equals(pirn.getItem().getRegistryName().toString())).map(Map.Entry::getValue).findAny().orElse("forge:white")
        ));

        // Pirns
        for (int i = 0; i < amountPirns; i++)
        {
            poseStack.pushPose();

            rotateAndTranslate(poseStack, facing, -0.78125, -1.625, 2);
            rotateForFacingMirrored(poseStack, facing, isMirrored);

            poseStack.mulPose(Vector3f.ZP.rotationDegrees(be.animation_pirn + 45.0F));

            poseStack.mulPose(Vector3f.ZN.rotationDegrees(45.0F * i));
            if (active)
                poseStack.mulPose(Vector3f.ZN.rotationDegrees(45.0F));

            RenderHelper.renderTexturedPirn(consumer, poseStack, -1, -5, 0, 1, -3, 9, pirnTexture, combinedLight);

            poseStack.popPose();
        }

        if (active)
        {
            poseStack.pushPose();

            rotateAndTranslate(poseStack, facing, -0.78125, -1.625, 2);
            rotateForFacingMirrored(poseStack, facing, isMirrored);

            poseStack.translate(be.animation_pirn_x - be.animation_pirn_x2, be.animation_pirn_y, -be.animation_pirn_z);

            poseStack.mulPose(Vector3f.ZP.rotationDegrees(be.animation_pirn + 45.0F));

            RenderHelper.renderTexturedPirn(consumer, poseStack, -1, -5, 0, 1, -3, 9, pirnTexture, combinedLight);

            poseStack.popPose();
        }

        PowerLoomRecipe recipe = PowerLoomRecipe.findRecipeForRendering(be.getLevel(), be.inventory.get(11));
        TextureAtlasSprite outputTexture = blockMap.getSprite(be.lastTexture);

        // Output Rod Cloth
        int amountOutput = be.inventory.get(12).getCount();

        if (amountOutput > 0)
        {
            int count = Math.floorDiv(amountOutput, 16);

            poseStack.pushPose();

            translateForFacing(poseStack, facing, -0.6875, 0.625, -0.5);
            rotateForFacingMirroredInverted(poseStack, facing, isMirrored);

            if (active)
                poseStack.mulPose(Vector3f.ZN.rotationDegrees(angle));

            RenderHelper.renderTexturedBox(consumer, poseStack, -2 - count / 2F, -2 - count / 2F, 0, 2 + count / 2F, 2 + count / 2F, 32, outputTexture, 0, 38, combinedLight);

            poseStack.popPose();
        }

        if (recipe != null)
        {
            TextureAtlasSprite texture = blockMap.getSprite(recipe.inProgressTexture);

            // Input Rod Cloth
            int amountWeave = 0;
            for (ItemStack weave : be.inputList)
                amountWeave += weave.getCount();

            if (amountWeave > 0)
            {
                int count = Math.floorDiv(amountWeave, (int) (0.75 * be.inputList.stream().filter(s -> !s.isEmpty()).findAny().orElse(ItemStack.EMPTY).getMaxStackSize()));

                poseStack.pushPose();

                translateForFacing(poseStack, facing, 1.6875, 0.625, -0.5);
                rotateForFacingMirroredInverted(poseStack, facing, isMirrored);

                if (active)
                    poseStack.mulPose(Vector3f.ZN.rotationDegrees(angle));

                RenderHelper.renderTexturedBox(consumer, poseStack, -2 - count / 2F, -2 - count / 2F, 0, 2 + count / 2F, 2 + count / 2F, 32, texture, 0, 38, combinedLight);

                poseStack.popPose();
            }

            if (be.inventory.get(11).getCount() >= recipe.secondaryInput.getCount())
            {
                // Rods
                poseStack.pushPose();

                translateForFacing(poseStack, facing, -0.6875, 1.5, -0.5);
                rotateForFacingMirroredInverted(poseStack, facing, isMirrored);

                RenderHelper.renderTexturedBox(consumer, poseStack, -2, -2, 0, 2, 2, 32, texture, 0, 38, combinedLight);

                poseStack.translate(2.3125, 0, 0);
                RenderHelper.renderTexturedBox(consumer, poseStack, -2, -2, 0, 2, 2, 32, texture, 0, 38, combinedLight);

                poseStack.popPose();

                // Cloths
                poseStack.pushPose();

                translateForFacing(poseStack, facing, -0.7, 0.7, -0.5);
                rotateForFacingMirroredInverted(poseStack, facing, isMirrored);
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(8.0F));
                RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 11, 32, texture, 0, 38 + be.animation_weave, combinedLight);

                poseStack.popPose();

                poseStack.pushPose();

                translateForFacing(poseStack, facing, 1.575, 0.7, -0.5);
                rotateForFacingMirroredInverted(poseStack, facing, isMirrored);
                poseStack.mulPose(Vector3f.ZN.rotationDegrees(8.0F));

                RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 12, 32, texture, 0, be.animation_weave, combinedLight);

                poseStack.popPose();

                // Threads
                for (int i = 0; i < 16; i++)
                {
                    poseStack.pushPose();

                    translateForFacing(poseStack, facing, -0.71875, 1.65, -0.5 + i * 0.125);
                    rotateForFacingMirroredInverted(poseStack, facing, isMirrored);
                    poseStack.mulPose(Vector3f.ZN.rotationDegrees(80.0F + be.angle_long_thread));

                    RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 27, 1, texture, 0, 0, combinedLight);

                    poseStack.popPose();

                    poseStack.pushPose();

                    translateForFacing(poseStack, facing, -0.71875, 1.65, -0.4375 + i * 0.125);
                    rotateForFacingMirroredInverted(poseStack, facing, isMirrored);
                    poseStack.mulPose(Vector3f.ZN.rotationDegrees(99.0F - be.angle_long_thread));

                    RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 27, 1, texture, 0, 0, combinedLight);

                    poseStack.popPose();

                    poseStack.pushPose();

                    translateForFacing(poseStack, facing, 1.635, 1.585, -0.5 + i * 0.125);
                    rotateForFacingMirroredInverted(poseStack, facing, isMirrored);
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(68.0F + be.angle_short_thread));

                    RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 13, 1, texture, 0, 0, combinedLight);

                    poseStack.popPose();

                    poseStack.pushPose();

                    translateForFacing(poseStack, facing, 1.635, 1.585, -0.4375 + i * 0.125);
                    rotateForFacingMirroredInverted(poseStack, facing, isMirrored);
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(110.0F - be.angle_short_thread));

                    RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 13, 1, texture, 0, 0, combinedLight);

                    poseStack.popPose();
                }
            }
        }
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

    private static void rotateRackForFacing(PoseStack stack, Direction facing)
    {
        if (facing == Direction.EAST)
            stack.mulPose(RenderHelpers.rotateDegreesX(180.0F));
        else
            stack.mulPose(RenderHelpers.rotateDegreesZ(180.0F));
        if (facing == Direction.WEST)
            stack.mulPose(RenderHelpers.rotateDegreesY(180.0F));
    }

    private static void rotateAndTranslate(PoseStack stack, Direction facing, double x, double y, double z)
    {
        rotateRackForFacing(stack, facing);

        if (facing == Direction.SOUTH)
            stack.translate(-2, 0, 0);
        if (facing == Direction.WEST)
            stack.translate(0, 0, -2);

        translateForFacing(stack, facing, x, y, z);
    }

    private static void rotateForFacingMirrored(PoseStack stack, Direction facing, boolean isMirrored)
    {
        if (isMirrored)
        {
            if (facing == Direction.NORTH || facing == Direction.SOUTH)
                stack.translate(1, 0, 0);
            if (facing == Direction.EAST || facing == Direction.WEST)
                stack.translate(0, 0, 1);
        }

        rotateForFacing(stack, facing);
    }

    private static void rotateForFacingMirroredInverted(PoseStack stack, Direction facing, boolean isMirrored)
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
