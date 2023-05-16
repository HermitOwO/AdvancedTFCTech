package com.hermitowo.advancedtfctech.client.screen;

import java.util.List;
import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import com.hermitowo.advancedtfctech.client.screen.elements.EnergyDisplay;
import com.hermitowo.advancedtfctech.common.blockentities.PowerLoomBlockEntity;
import com.hermitowo.advancedtfctech.common.container.PowerLoomContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

// For Debugging
public class PowerLoomScreen extends IEContainerScreen<PowerLoomContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(ImmersiveEngineering.MODID, "textures/gui/arc_furnace.png");

    PowerLoomBlockEntity tile;

    public PowerLoomScreen(PowerLoomContainer container, Inventory playerInventory, Component title)
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
        return List.of(new EnergyDisplay(this.leftPos + 157, this.topPos + 22, 7, 46, this.tile.energyStorage));
    }

    @Override
    protected void drawContainerBackgroundPre(@Nonnull PoseStack transform, float f, int mx, int my)
    {
        for (MultiblockProcess<?> process : tile.processQueue)
            if (process instanceof MultiblockProcessInMachine<?> inMachine)
            {
                float mod = process.processTick / (float)process.getMaxTicks(tile.getLevel());
                int slot = inMachine.getInputSlots()[0];
                int h = (int)Math.max(1, mod * 16);
                this.blit(transform, leftPos + 27 + slot % 3 * 21, topPos + 34 + slot / 3 * 18 + (16 - h), 176, 16 - h, 2, h);
            }
    }
}
