package com.hermitowo.advancedtfctech.client.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.registry.MultiblockBlockEntityMaster;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockOrientation;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.client.model.PowerLoomParts;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.PowerLoomLogic;
import com.hermitowo.advancedtfctech.common.recipes.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hermitowo.advancedtfctech.common.multiblocks.logic.PowerLoomLogic.*;

public class PowerLoomRenderer extends IEBlockEntityRenderer<MultiblockBlockEntityMaster<PowerLoomLogic.State>>
{
    public static final Material material = new Material(InventoryMenu.BLOCK_ATLAS, AdvancedTFCTech.rl("block/multiblock/power_loom"));

    private final PowerLoomParts powerLoomModel;

    public PowerLoomRenderer(BlockEntityRendererProvider.Context context)
    {
        this.powerLoomModel = new PowerLoomParts(context.bakeLayer(PowerLoomParts.LAYER_LOCATION));
    }

    @Override
    public void render(MultiblockBlockEntityMaster<PowerLoomLogic.State> be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final IMultiblockContext<PowerLoomLogic.State> ctx = be.getHelper().getContext();
        final PowerLoomLogic.State state = ctx.getState();
        final MultiblockOrientation orientation = ctx.getLevel().getOrientation();
        Direction facing = orientation.front();

        boolean active = state.shouldRenderAsActive();
        float angle = state.rodAngle + (active ? 1.75F * partialTicks : 0);
        buffer = RenderHelper.mirror(orientation, poseStack, buffer);
        VertexConsumer vertexConsumer = material.buffer(buffer, RenderType::entitySolid);

        // Output Rod
        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        RenderHelper.translate(poseStack, -0.6875, 0.625, -0.6875);

        poseStack.mulPose(Axis.ZN.rotationDegrees(angle));
        powerLoomModel.rod.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Input Rod
        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        RenderHelper.translate(poseStack, 1.6875, 0.625, -0.6875);

        poseStack.mulPose(Axis.ZN.rotationDegrees(angle));
        powerLoomModel.rod.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Horizontal Rack
        float rack = state.rackDispl;
        rack = rack * rack * (3.0F - 2.0F * rack);

        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        RenderHelper.translate(poseStack, -1.211 - rack, 0, 1.9375);

        powerLoomModel.rack.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.translate(6.9375, 0, -0.25 + state.rackSideDispl);

        powerLoomModel.rack_side.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // High Rack
        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        RenderHelper.translate(poseStack, -1.9375, 0.4375 - state.rack2Displ, 1.9375);

        powerLoomModel.rack2.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Low Rack
        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        RenderHelper.translate(poseStack, -1.9375, -0.4375 + state.rack2Displ, 1.9375);

        powerLoomModel.rack3.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        // Holder
        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        RenderHelper.translate(poseStack, -0.78125, -1.625, 2);

        float tilt = state.holderRotation * 45.0F;
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.pirnAngle + tilt));
        powerLoomModel.holder.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();

        /*
         *
         */

        VertexConsumer consumer = buffer.getBuffer(RenderType.solid());

        List<ItemStack> pirnList = new ArrayList<>(8);
        int amountPirns = 0;
        for (int i = FIRST_PIRN_IN_SLOT; i < FIRST_PIRN_IN_SLOT + PIRN_IN_SLOT_COUNT; i++)
        {
            pirnList.add(state.inventory.getStackInSlot(i));
            amountPirns += state.inventory.getStackInSlot(i).getCount();
        }
        ItemStack pirn = pirnList.stream().filter(s -> !s.isEmpty()).findAny().orElse(ItemStack.EMPTY);
        amountPirns = active ? amountPirns - 1 : amountPirns;

        Map<String, String> pirnTextures = new HashMap<>();
        pirnTextures.put("advancedtfctech:fiber_winded_pirn", "advancedtfctech:block/multiblock/power_loom/fiber_winded_pirn");
        pirnTextures.put("advancedtfctech:silk_winded_pirn", "advancedtfctech:block/multiblock/power_loom/wool_winded_pirn");
        pirnTextures.put("advancedtfctech:wool_winded_pirn", "advancedtfctech:block/multiblock/power_loom/wool_winded_pirn");
        pirnTextures.put("advancedtfctech:pineapple_winded_pirn", "advancedtfctech:block/multiblock/power_loom/pineapple_winded_pirn");

        Map<String, String> configTextures =
            ATTConfig.CLIENT.additionalPowerLoomPirnTextures.get().stream().collect(Collectors.toMap(list -> list.get(0), list -> list.get(1)));

        pirnTextures.putAll(configTextures);

        TextureAtlasSprite pirnTexture = ClientUtils.getSprite(new ResourceLocation(
            pirnTextures.entrySet().stream().filter(entry -> entry.getKey().equals(ForgeRegistries.ITEMS.getKey(pirn.getItem()).toString())).map(Map.Entry::getValue).findAny().orElse("forge:white")
        ));

        // Pirns
        for (int i = 0; i < amountPirns; i++)
        {
            poseStack.pushPose();

            rotateForFacing(poseStack, facing);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            RenderHelper.translate(poseStack, -0.78125, -1.625, 2);

            poseStack.mulPose(Axis.ZP.rotationDegrees(state.pirnAngle + 45.0F));

            poseStack.mulPose(Axis.ZN.rotationDegrees(45.0F * i));
            if (active)
                poseStack.mulPose(Axis.ZN.rotationDegrees(45.0F));

            RenderHelper.renderTexturedPirn(consumer, poseStack, -1, -5, 0, 1, -3, 9, pirnTexture, combinedLight);

            poseStack.popPose();
        }

        if (active)
        {
            poseStack.pushPose();

            rotateForFacing(poseStack, facing);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            RenderHelper.translate(poseStack, -0.78125, -1.625, 2);

            poseStack.translate(state.pirnDisplX - state.pirnDisplX2, state.pirnDisplY, -state.pirnDisplZ);

            poseStack.mulPose(Axis.ZP.rotationDegrees(state.pirnAngle + 45.0F));
            RenderHelper.renderTexturedPirn(consumer, poseStack, -1, -5, 0, 1, -3, 9, pirnTexture, combinedLight);

            poseStack.popPose();
        }

        PowerLoomRecipe recipe = PowerLoomRecipe.findRecipeForRendering(ctx.getLevel().getRawLevel(), state.inventory.getStackInSlot(SECONDARY_WEAVE_IN_SLOT));
        TextureAtlasSprite outputTexture = ClientUtils.getSprite(state.lastTexture);

        // Output Rod Cloth
        int amountOutput = state.inventory.getStackInSlot(OUT_SLOT).getCount();

        if (amountOutput > 0)
        {
            int count = Math.floorDiv(amountOutput, 16);

            poseStack.pushPose();

            rotateForFacing(poseStack, facing);
            RenderHelper.translate(poseStack, -0.6875, 0.625, -0.5);

            poseStack.mulPose(Axis.ZN.rotationDegrees(angle));
            RenderHelper.renderTexturedBox(consumer, poseStack, -2 - count / 2F, -2 - count / 2F, 0, 2 + count / 2F, 2 + count / 2F, 32, outputTexture, 0, 38, combinedLight);

            poseStack.popPose();
        }

        if (recipe != null)
        {
            TextureAtlasSprite texture = ClientUtils.getSprite(recipe.inProgressTexture);

            // Input Rod Cloth
            int amountWeave = 0;
            List<ItemStack> inputList = new ArrayList<>(3);
            for (int i = FIRST_WEAVE_IN_SLOT; i < FIRST_WEAVE_IN_SLOT + WEAVE_IN_SLOT_COUNT; i++)
                inputList.add(state.inventory.getStackInSlot(i));
            for (ItemStack weave : inputList)
                amountWeave += weave.getCount();

            if (amountWeave > 0)
            {
                int count = Math.floorDiv(amountWeave, (int) (0.75 * inputList.stream().filter(s -> !s.isEmpty()).findAny().orElse(ItemStack.EMPTY).getMaxStackSize()));

                poseStack.pushPose();

                rotateForFacing(poseStack, facing);
                RenderHelper.translate(poseStack, 1.6875, 0.625, -0.5);

                poseStack.mulPose(Axis.ZN.rotationDegrees(angle));
                RenderHelper.renderTexturedBox(consumer, poseStack, -2 - count / 2F, -2 - count / 2F, 0, 2 + count / 2F, 2 + count / 2F, 32, texture, 0, 38, combinedLight);

                poseStack.popPose();
            }

            if (state.inventory.getStackInSlot(SECONDARY_WEAVE_IN_SLOT).getCount() >= recipe.secondaryInput.getCount())
            {
                // Rods
                poseStack.pushPose();

                rotateForFacing(poseStack, facing);
                RenderHelper.translate(poseStack, -0.6875, 1.5, -0.5);

                RenderHelper.renderTexturedBox(consumer, poseStack, -2, -2, 0, 2, 2, 32, texture, 0, 38, combinedLight);

                poseStack.translate(2.3125, 0, 0);
                RenderHelper.renderTexturedBox(consumer, poseStack, -2, -2, 0, 2, 2, 32, texture, 0, 38, combinedLight);

                poseStack.popPose();

                // Cloths
                poseStack.pushPose();

                rotateForFacing(poseStack, facing);
                RenderHelper.translate(poseStack, -0.7, 0.7, -0.5);

                poseStack.mulPose(Axis.ZP.rotationDegrees(8.0F));
                RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 11, 32, texture, 0, 38 + state.weaveTextureDispl, combinedLight);

                poseStack.popPose();

                poseStack.pushPose();

                rotateForFacing(poseStack, facing);
                RenderHelper.translate(poseStack, 1.575, 0.7, -0.5);

                poseStack.mulPose(Axis.ZN.rotationDegrees(8.0F));
                RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 12, 32, texture, 0, state.weaveTextureDispl, combinedLight);

                poseStack.popPose();

                // Threads
                for (int i = 0; i < 16; i++)
                {
                    poseStack.pushPose();

                    rotateForFacing(poseStack, facing);
                    RenderHelper.translate(poseStack, -0.71875, 1.65, -0.5 + i * 0.125);

                    poseStack.mulPose(Axis.ZN.rotationDegrees(80.0F + state.longThreadAngle));
                    RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 27, 1, texture, 0, 0, combinedLight);

                    poseStack.popPose();

                    poseStack.pushPose();

                    rotateForFacing(poseStack, facing);
                    RenderHelper.translate(poseStack, -0.71875, 1.65, -0.4375 + i * 0.125);

                    poseStack.mulPose(Axis.ZN.rotationDegrees(99.0F - state.longThreadAngle));
                    RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 27, 1, texture, 0, 0, combinedLight);

                    poseStack.popPose();

                    poseStack.pushPose();

                    rotateForFacing(poseStack, facing);
                    RenderHelper.translate(poseStack, 1.635, 1.585, -0.5 + i * 0.125);

                    poseStack.mulPose(Axis.ZP.rotationDegrees(68.0F + state.shortThreadAngle));
                    RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 13, 1, texture, 0, 0, combinedLight);

                    poseStack.popPose();

                    poseStack.pushPose();

                    rotateForFacing(poseStack, facing);
                    RenderHelper.translate(poseStack, 1.635, 1.585, -0.4375 + i * 0.125);

                    poseStack.mulPose(Axis.ZP.rotationDegrees(110.0F - state.shortThreadAngle));
                    RenderHelper.renderTexturedBox(consumer, poseStack, 0, 0, 0, 1, 13, 1, texture, 0, 0, combinedLight);

                    poseStack.popPose();
                }
            }
        }
    }
}
