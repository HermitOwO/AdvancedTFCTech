package com.hermitowo.advancedtfctech.client.render;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import blusunrize.immersiveengineering.api.client.IVertexBufferHolder;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import blusunrize.immersiveengineering.client.render.tile.SawmillRenderer;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FleshingMachineRenderer extends IEBlockEntityRenderer<FleshingMachineBlockEntity>
{
    private static final IVertexBufferHolder BLADES_BUFFER = IVertexBufferHolder.create(() -> SawmillRenderer.BLADE.getNullQuads());

    @Override
    public void render(FleshingMachineBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (be.isDummy() || !be.getLevelNonnull().hasChunkAt(be.getBlockPos()))
            return;

        Direction facing = be.getFacing();
        VertexConsumer consumer = buffer.getBuffer(RenderType.solid());
        TextureAtlasSprite rodTexture = ClientUtils.getSprite(new ResourceLocation("advancedtfctech:block/metal_device/fleshing_machine"));
        float bladeRotation = be.bladeAngle + (be.getIsActive() ? 36F * partialTicks : 0);
        float hideRotation = be.rodAngle + (be.getIsActive() ? 9F * partialTicks : 0);

        // Blade
        ItemStack blade = be.getInventory().get(1);
        if (!blade.isEmpty())
        {
            for (int i = 0; i < 20; i++)
            {
                poseStack.pushPose();

                rotateForFacing(poseStack, facing);
                RenderHelper.translate(poseStack, 0.0625 + 0.0625 * i, 0.6875, 0.4375);

                poseStack.mulPose(Axis.YP.rotationDegrees(90));

                poseStack.scale(0.125F, 0.125F, 0.125F);
                poseStack.scale(1.1F, 1.1F, 4F);

                poseStack.mulPose(Axis.ZP.rotationDegrees(bladeRotation));
                BLADES_BUFFER.render(RenderType.solid(), combinedLight, combinedOverlay, buffer, poseStack);

                poseStack.popPose();
            }
        }

        // Blade rod
        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        RenderHelper.translate(poseStack, 0.25, 0.6875, 0.4375);

        poseStack.mulPose(Axis.XP.rotationDegrees(bladeRotation));

        RenderHelper.renderTexturedBox(consumer, poseStack, 0, -1, -1, 21, 1, 1, rodTexture, 0, 21, combinedLight);

        poseStack.popPose();

        // Hide
        ItemStack hide = be.getInventory().get(0);
        if (!hide.isEmpty())
        {
            Map<String, String> hideTextures = new HashMap<>();
            hideTextures.put("tfc:small_soaked_hide", "advancedtfctech:block/metal_device/fleshing_machine/soaked");
            hideTextures.put("tfc:medium_soaked_hide", "advancedtfctech:block/metal_device/fleshing_machine/soaked");
            hideTextures.put("tfc:large_soaked_hide", "advancedtfctech:block/metal_device/fleshing_machine/soaked");
            hideTextures.put("tfc:small_scraped_hide", "advancedtfctech:block/metal_device/fleshing_machine/scraped");
            hideTextures.put("tfc:medium_scraped_hide", "advancedtfctech:block/metal_device/fleshing_machine/scraped");
            hideTextures.put("tfc:large_scraped_hide", "advancedtfctech:block/metal_device/fleshing_machine/scraped");

            Map<String, String> configTextures =
                ATTConfig.CLIENT.additionalFleshingMachineTextures.get().stream().collect(Collectors.toMap(list -> list.get(0), list -> list.get(1)));

            hideTextures.putAll(configTextures);

            TextureAtlasSprite hideTexture = ClientUtils.getSprite(new ResourceLocation(
                hideTextures.entrySet().stream().filter(entry -> entry.getKey().equals(ForgeRegistries.ITEMS.getKey(hide.getItem()).toString())).map(Map.Entry::getValue).findAny().orElse("forge:white")
            ));

            poseStack.pushPose();

            rotateForFacing(poseStack, facing);
            RenderHelper.translate(poseStack, 0.28125, 0.5, 0.625);

            poseStack.mulPose(Axis.XN.rotationDegrees(hideRotation));

            RenderHelper.renderTexturedBox(consumer, poseStack, 0, -2, -2, 20, 2, 2, hideTexture, combinedLight);

            poseStack.popPose();
        }

        // Input rod
        poseStack.pushPose();

        rotateForFacing(poseStack, facing);
        RenderHelper.translate(poseStack, 0.25, 0.5, 0.625);

        poseStack.mulPose(Axis.XN.rotationDegrees(hideRotation));

        RenderHelper.renderTexturedBox(consumer, poseStack, 0, -1, -1, 21, 1, 1, rodTexture, 0, 21, combinedLight);

        poseStack.popPose();
    }

    public static void reset()
    {
        BLADES_BUFFER.reset();
    }
}
