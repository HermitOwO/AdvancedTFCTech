package com.hermitowo.advancedtfctech.client.screen;

import java.util.List;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import com.hermitowo.advancedtfctech.client.screen.elements.EnergyDisplay;
import com.hermitowo.advancedtfctech.common.blockentities.GristMillBlockEntity;
import com.hermitowo.advancedtfctech.common.container.GristMillContainer;
import com.hermitowo.advancedtfctech.common.container.GristMillContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class GristMillScreen extends IEContainerScreen<GristMillContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/thresher.png");

    GristMillBlockEntity tile;

    public GristMillScreen(GristMillContainer container, Inventory playerInventory, Component title)
    {
        super(container, playerInventory, title, TEXTURE);
        this.tile = menu.tile;

        this.imageWidth = 176;
        this.imageHeight = 207;
    }

    @Override
    protected void renderLabels(PoseStack transform, int mouseX, int mouseY)
    {
        // Render no labels
    }

    @Nonnull
    @Override
    protected List<InfoArea> makeInfoAreas()
    {
        return List.of(new EnergyDisplay(this.leftPos + 157, this.topPos + 40, 7, 46, this.tile.energyStorage));
    }

    @Override
    protected void drawContainerBackgroundPre(@Nonnull PoseStack transform, float f, int mx, int my)
    {
        int c = 0;
        for(MultiblockProcess<?> process : tile.processQueue)
            if(process instanceof MultiblockProcessInMachine<?>)
            {
                float mod = process.processTick / (float)process.getMaxTicks(tile.getLevel());
                int dispX = (c % 3) * 32;
                int dispY = (c / 3) * 43;
                int h = (int)Math.max(1, mod * 16);
                this.blit(transform, leftPos + 77 + dispX, topPos + 54 + dispY, 198, 100, 22, h);
            }
    }

}
