package com.hermitowo.advancedtfctech.client.screen;

import java.util.List;
import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.hermitowo.advancedtfctech.client.screen.elements.EnergyDisplay;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import com.hermitowo.advancedtfctech.common.container.FleshingMachineContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

// For Debugging
public class FleshingMachineScreen extends IEContainerScreen<FleshingMachineContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(ImmersiveEngineering.MODID, "textures/gui/arc_furnace.png");
    private final FleshingMachineBlockEntity tile;

    public FleshingMachineScreen(FleshingMachineContainer container, Inventory playerInventory, Component title)
    {
        super(container, playerInventory, title, TEXTURE);
        this.tile = container.tile;

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
        return List.of(new EnergyDisplay(leftPos + 157, topPos + 22, 7, 46, tile.energyStorage));
    }
}
