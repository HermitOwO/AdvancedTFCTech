package com.hermitowo.advancedtfctech.client.screen;

import java.util.List;
import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.hermitowo.advancedtfctech.common.container.FleshingMachineContainer;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

// For Debugging
public class FleshingMachineScreen extends IEContainerScreen<FleshingMachineContainer>
{
    private static final ResourceLocation TEXTURE = ImmersiveEngineering.rl("textures/gui/arc_furnace.png");

    public FleshingMachineScreen(FleshingMachineContainer container, Inventory playerInventory, Component title)
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
        return List.of(new EnergyInfoArea(leftPos + 157, topPos + 22, menu.energy));
    }
}
