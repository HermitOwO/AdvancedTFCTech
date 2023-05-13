package com.hermitowo.advancedtfctech.client.utils;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MCUtil
{
    public static void bindTexture(ResourceLocation texture)
    {
        RenderSystem.setShaderTexture(0, texture);
    }

    public static Screen getScreen()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.screen;
    }

    public static ParticleEngine getParticleEngine()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.particleEngine;
    }

    public static TextureManager getTextureManager()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.textureManager;
    }

    public static BlockRenderDispatcher getBlockRenderer()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.getBlockRenderer();
    }

    public static GameRenderer getGameRenderer()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.gameRenderer;
    }

    public static ClientLevel getLevel()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.level;
    }

    public static Font getFont()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.font;
    }

    public static LocalPlayer getPlayer()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.player;
    }

    public static HitResult getHitResult()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.hitResult;
    }

    public static Options getOptions()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.options;
    }

    public static Window getWindow()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.getWindow();
    }

    public static ItemRenderer getItemRenderer()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.getItemRenderer();
    }
}
