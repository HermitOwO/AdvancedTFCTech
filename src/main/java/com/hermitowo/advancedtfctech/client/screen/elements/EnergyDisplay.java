package com.hermitowo.advancedtfctech.client.screen.elements;

import java.util.List;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyDisplay extends InfoArea
{
    private final IEnergyStorage storage;

    public EnergyDisplay(int x, int y, int width, int height, IEnergyStorage storage)
    {
        super(new Rect2i(x, y, width, height));
        this.storage = storage;
    }

    @Override
    protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip)
    {
        tooltip.add(new TextComponent(this.storage.getEnergyStored() + "/" + this.storage.getMaxEnergyStored() + " IF"));
    }

    @Override
    public void draw(PoseStack transform)
    {
        final int height = this.area.getHeight();
        int stored = (int) (height * (this.storage.getEnergyStored() / (float) this.storage.getMaxEnergyStored()));
        fillGradient(transform,
            this.area.getX(), this.area.getY() + (height - stored),
            this.area.getX() + this.area.getWidth(), this.area.getY() + this.area.getHeight(),
            0xFFB51500, 0xFF600B00
        );
    }
}
