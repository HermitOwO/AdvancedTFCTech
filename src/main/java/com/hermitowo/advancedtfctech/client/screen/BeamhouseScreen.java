package com.hermitowo.advancedtfctech.client.screen;

import java.util.List;
import java.util.function.Consumer;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonIE;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import com.google.common.collect.ImmutableList;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.container.BeamhouseContainer;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;

public class BeamhouseScreen extends IEContainerScreen<BeamhouseContainer>
{
    private static final ResourceLocation TEXTURE = AdvancedTFCTech.rl("textures/gui/beamhouse.png");
    private GuiButtonIE distributeButton;

    public BeamhouseScreen(BeamhouseContainer container, Inventory playerInventory, Component title)
    {
        super(container, playerInventory, title, TEXTURE);

        this.imageWidth = 176;
        this.imageHeight = 184;
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
        return ImmutableList.of(
            new EnergyInfoArea(leftPos + 157, topPos + 31, menu.energy),
            new FluidInfoArea(menu.tank, new Rect2i(leftPos + 111, topPos + 30, 16, 47), 176, 32, 20, 51, TEXTURE)
        );
    }

    @Override
    protected void gatherAdditionalTooltips(int mouseX, int mouseY, Consumer<Component> addLine, Consumer<Component> addGray)
    {
        super.gatherAdditionalTooltips(mouseX, mouseY, addLine, addGray);
        if (distributeButton.isHoveredOrFocused())
            addLine.accept(Component.translatable("advancedtfctech.gui.distribute"));
    }

    @Override
    protected void drawContainerBackgroundPre(@Nonnull GuiGraphics graphics, float f, int mx, int my)
    {
        for (BeamhouseContainer.ProcessSlot process : menu.processes.get())
        {
            int slot = process.slot();
            int h = process.processStep();
            graphics.blit(background, leftPos + 33 + slot % 4 * 21, topPos + 13 + slot / 4 * 18 + (16 - h), 176, 16 - h, 2, h);
        }
    }

    @Override
    public void init()
    {
        super.init();
        distributeButton = new GuiButtonIE(leftPos + 111, topPos + 9, 16, 16, Component.empty(), TEXTURE, 179, 0,
            button -> {
                if (menu.getCarried().isEmpty())
                    autoSplitStacks();
            })
        {
            @Override
            public boolean isHoveredOrFocused()
            {
                return super.isHoveredOrFocused() && menu.getCarried().isEmpty();
            }
        }.setHoverOffset(0, 16);
        this.addRenderableWidget(distributeButton);
    }

    private void autoSplitStacks()
    {
        int emptySlot;
        int largestSlot;
        int largestCount;
        for (int j = 0; j < 12; j++)
        {
            emptySlot = -1;
            largestSlot = -1;
            largestCount = -1;
            for (int i = 0; i < 12; i++)
                if (menu.getSlot(i).hasItem())
                {
                    int count = menu.getSlot(i).getItem().getCount();
                    if (count > 1 && count > largestCount)
                    {
                        largestSlot = i;
                        largestCount = count;
                    }
                }
                else if (emptySlot < 0)
                    emptySlot = i;
            if (emptySlot >= 0 && largestSlot >= 0)
            {
                this.slotClicked(menu.getSlot(largestSlot), largestSlot, 1, ClickType.PICKUP);
                this.slotClicked(menu.getSlot(emptySlot), emptySlot, 0, ClickType.PICKUP);
            }
            else
                break;
        }
    }
}
