package com.hermitowo.advancedtfctech.client.screen;

import java.util.List;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.container.GristMillContainer;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GristMillScreen extends IEContainerScreen<GristMillContainer>
{
    private static final ResourceLocation TEXTURE = AdvancedTFCTech.rl("textures/gui/thresher.png");

    public GristMillScreen(GristMillContainer container, Inventory playerInventory, Component title)
    {
        super(container, playerInventory, title, TEXTURE);

        this.imageWidth = 176;
        this.imageHeight = 207;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        // Render no labels
    }

    @Nonnull
    @Override
    protected List<InfoArea> makeInfoAreas()
    {
        return List.of(new EnergyInfoArea(leftPos + 157, topPos + 40, menu.energy));
    }

    @Override
    protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my)
    {
        for (GristMillContainer.ProcessSlot process : menu.processes.get())
        {
            int h = process.processStep();
            graphics.blit(background, leftPos + 77, topPos + 54, 198, 100, 22, h);
        }
    }
}
