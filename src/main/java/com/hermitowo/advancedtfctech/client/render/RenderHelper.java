package com.hermitowo.advancedtfctech.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class RenderHelper
{
    // Pixel-based
    public static void renderTexturedBox(VertexConsumer wr, PoseStack stack, float x0, float y0, float z0, float x1, float y1, float z1, TextureAtlasSprite texture, float minU, float minV, int light)
    {
        float pixel = (texture.getU1() - texture.getU0()) / texture.getWidth();
        float u0 = texture.getU0() + minU * pixel;
        float v0 = texture.getV0() + minV * pixel;

        renderBox(wr, stack, x0 / 16, y0 / 16, z0 / 16, x1 / 16, y1 / 16, z1 / 16, u0, v0, pixel, light);
    }

    public static void renderBox(VertexConsumer wr, PoseStack stack, float x0, float y0, float z0, float x1, float y1, float z1, float u0, float v0, float pixel, int light)
    {
        float normalX = 0;
        float normalY = 0;
        float normalZ = 1;

        float dX = (x1 - x0) * 16 * pixel;
        float dY = (y1 - y0) * 16 * pixel;
        float dZ = (z1 - z0) * 16 * pixel;

        putVertex(wr, stack, x0, y0, z1, u0, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z1, u0 + dX, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z1, u0 + dX, v0 + dY, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y1, z1, u0, v0 + dY, normalX, normalY, normalZ, light);
        normalZ = -1;
        putVertex(wr, stack, x0, y1, z0, u0, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z0, u0 + dX, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z0, u0 + dX, v0 + dY, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y0, z0, u0, v0 + dY, normalX, normalY, normalZ, light);

        normalZ = 0;
        normalY = -1;
        putVertex(wr, stack, x0, y0, z0, u0, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z0, u0 + dX, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z1, u0 + dX, v0 + dZ, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y0, z1, u0, v0 + dZ, normalX, normalY, normalZ, light);
        normalY = 1;
        putVertex(wr, stack, x0, y1, z1, u0, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z1, u0 + dX, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z0, u0 + dX, v0 + dZ, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y1, z0, u0, v0 + dZ, normalX, normalY, normalZ, light);

        normalY = 0;
        normalX = -1;
        putVertex(wr, stack, x0, y0, z0, u0, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y0, z1, u0 + dZ, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y1, z1, u0 + dZ, v0 + dY, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y1, z0, u0, v0 + dY, normalX, normalY, normalZ, light);
        normalX = 1;
        putVertex(wr, stack, x1, y1, z0, u0, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z1, u0 + dZ, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z1, u0 + dZ, v0 + dY, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z0, u0, v0 + dY, normalX, normalY, normalZ, light);
    }

    public static void renderTexturedPirn(VertexConsumer wr, PoseStack stack, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, TextureAtlasSprite texture, int light)
    {
        float pixel = (texture.getU1() - texture.getU0()) / texture.getWidth();
        float u0 = texture.getU0();
        float v0 = texture.getV0();

        float x0 = minX / 16;
        float y0 = minY / 16;
        float z0 = minZ / 16;
        float x1 = maxX / 16;
        float y1 = maxY / 16;
        float z1 = maxZ / 16;

        float dX = (maxX - minX) * pixel;
        float dY = (maxY - minY) * pixel;
        float dZ = (maxZ - minZ) * pixel;

        float s = dX + Math.abs(dY - dZ);

        float normalX = 0;
        float normalY = 0;
        float normalZ = 1;

        putVertex(wr, stack, x0, y0, z1, u0 + s, v0 + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z1, u0 + dX + s, v0 + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z1, u0 + dX + s, v0 + dY + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y1, z1, u0 + s, v0 + dY + s, normalX, normalY, normalZ, light);
        normalZ = -1;
        putVertex(wr, stack, x0, y1, z0, u0 + s , v0 + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z0, u0 + dX + s, v0 + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z0, u0 + dX + s, v0 + dY + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y0, z0, u0 + s, v0 + dY + s, normalX, normalY, normalZ, light);

        normalZ = 0;
        normalY = -1;
        putVertex(wr, stack, x0, y0, z0, u0 + s, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z0, u0 + dX + s, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z1, u0 + dX + s, v0 + dZ, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y0, z1, u0 + s, v0 + dZ, normalX, normalY, normalZ, light);
        normalY = 1;
        putVertex(wr, stack, x0, y1, z1, u0 + s, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z1, u0 + dX + s, v0, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z0, u0 + dX + s, v0 + dZ, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y1, z0, u0 + s, v0 + dZ, normalX, normalY, normalZ, light);

        normalY = 0;
        normalX = -1;
        putVertex(wr, stack, x0, y0, z0, u0, v0 + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y0, z1, u0 + dZ, v0 + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y1, z1, u0 + dZ, v0 + dY + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x0, y1, z0, u0, v0 + dY + s, normalX, normalY, normalZ, light);
        normalX = 1;
        putVertex(wr, stack, x1, y1, z0, u0, v0 + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y1, z1, u0 + dZ, v0 + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z1, u0 + dZ, v0 + dY + s, normalX, normalY, normalZ, light);
        putVertex(wr, stack, x1, y0, z0, u0, v0 + dY + s, normalX, normalY, normalZ, light);
    }

    private static void putVertex(VertexConsumer b, PoseStack mat, float x, float y, float z, float u, float v, float nX, float nY, float nZ, int light)
    {
        b.vertex(mat.last().pose(), x, y, z)
            .color(1F, 1F, 1F, 1F)
            .uv(u, v)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(light)
            .normal(mat.last().normal(), nX, nY, nZ)
            .endVertex();
    }
}
