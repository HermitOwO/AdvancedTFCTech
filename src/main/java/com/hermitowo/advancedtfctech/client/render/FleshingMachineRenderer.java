package com.hermitowo.advancedtfctech.client.render;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import blusunrize.immersiveengineering.api.client.IVertexBufferHolder;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import blusunrize.immersiveengineering.client.render.tile.SawmillRenderer;
import com.hermitowo.advancedtfctech.client.model.DynamicModel;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

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
        TextureAtlas blockMap = ClientUtils.mc().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite rodTexture = blockMap.getSprite(new ResourceLocation("advancedtfctech:metal_device/fleshing_machine"));
        float bladeRotation = be.animation_bladeRotation + (be.getIsActive() ? 36F * partialTicks : 0);
        float hideRotation = be.animation_rodRotation + (be.getIsActive() ? 9F * partialTicks : 0);

        // Blade
        ItemStack blade = be.getInventory().get(1);
        if (!blade.isEmpty())
        {
            for (int i = 0; i < 20; i++)
            {
                poseStack.pushPose();

                translateForFacing(poseStack, facing, 0.0625 + 0.0625 * i, 0.6875, 0.4375);

                rotateForFacing(poseStack, facing);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(90));

                poseStack.scale(0.125F, 0.125F, 0.125F);
                poseStack.scale(1.1F, 1.1F, 4F);

                poseStack.mulPose(Vector3f.ZP.rotationDegrees(bladeRotation));

                BLADES_BUFFER.render(RenderType.solid(), combinedLight, combinedOverlay, buffer, poseStack);

                poseStack.popPose();
            }
        }

        // Blade rod
        poseStack.pushPose();

        translateForFacing(poseStack, facing, 0.25, 0.6875, 0.4375);
        rotateForFacing(poseStack, facing);

        poseStack.mulPose(Vector3f.XP.rotationDegrees(bladeRotation));

        RenderHelper.renderTexturedBox(consumer, poseStack, 0, -1, -1, 21, 1, 1, rodTexture, 86, 62, combinedLight);

        poseStack.popPose();

        // Hide
        ItemStack hide = be.getInventory().get(0);
        if (!hide.isEmpty())
        {
            Map<String, String> hideTextures = new HashMap<>();
            hideTextures.put("tfc:small_soaked_hide", "advancedtfctech:metal_device/fleshing_machine/soaked");
            hideTextures.put("tfc:medium_soaked_hide", "advancedtfctech:metal_device/fleshing_machine/soaked");
            hideTextures.put("tfc:large_soaked_hide", "advancedtfctech:metal_device/fleshing_machine/soaked");
            hideTextures.put("tfc:small_scraped_hide", "advancedtfctech:metal_device/fleshing_machine/scraped");
            hideTextures.put("tfc:medium_scraped_hide", "advancedtfctech:metal_device/fleshing_machine/scraped");
            hideTextures.put("tfc:large_scraped_hide", "advancedtfctech:metal_device/fleshing_machine/scraped");

            Map<String, String> configTextures =
                ATTConfig.CLIENT.additionalFleshingMachineTextures.get().stream().collect(Collectors.toMap(list -> list.get(0), list -> list.get(1)));

            hideTextures.putAll(configTextures);

            TextureAtlasSprite hideTexture = blockMap.getSprite(new ResourceLocation(
                hideTextures.entrySet().stream().filter(entry -> entry.getKey().equals(hide.getItem().getRegistryName().toString())).map(Map.Entry::getValue).findAny().orElse("forge:white")
            ));

            poseStack.pushPose();

            translateForFacing(poseStack, facing, 0.28125, 0.5, 0.625);
            rotateForFacing(poseStack, facing);
            poseStack.mulPose(Vector3f.XN.rotationDegrees(hideRotation));

            RenderHelper.renderTexturedBox(consumer, poseStack, 0, -2, -2, 20, 2, 2, hideTexture, combinedLight);

            poseStack.popPose();
        }

        // Input rod
        poseStack.pushPose();

        translateForFacing(poseStack, facing, 0.25, 0.5, 0.625);
        rotateForFacing(poseStack, facing);

        poseStack.mulPose(Vector3f.XN.rotationDegrees(hideRotation));

        RenderHelper.renderTexturedBox(consumer, poseStack, 0, -1, -1, 21, 1, 1, rodTexture, 86, 62, combinedLight);

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

    public static void reset()
    {
        BLADES_BUFFER.reset();
    }
}
