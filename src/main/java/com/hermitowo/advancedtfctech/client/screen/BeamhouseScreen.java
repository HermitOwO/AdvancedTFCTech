package com.hermitowo.advancedtfctech.client.screen;

import java.util.List;
import java.util.function.Consumer;
import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonIE;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import com.google.common.collect.ImmutableList;
import com.hermitowo.advancedtfctech.client.screen.elements.EnergyDisplay;
import com.hermitowo.advancedtfctech.common.blockentities.BeamhouseBlockEntity;
import com.hermitowo.advancedtfctech.common.container.BeamhouseContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class BeamhouseScreen extends IEContainerScreen<BeamhouseContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/beamhouse.png");
    private final BeamhouseBlockEntity tile;
    private GuiButtonIE distributeButton;

    public BeamhouseScreen(BeamhouseContainer container, Inventory playerInventory, Component title)
    {
        super(container, playerInventory, title, TEXTURE);
        this.tile = container.tile;

        this.imageWidth = 176;
        this.imageHeight = 184;
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
        return ImmutableList.of(
            new EnergyDisplay(leftPos + 157, topPos + 31, 7, 46, tile.energyStorage),
            new FluidInfoArea(tile.tank, new Rect2i(leftPos + 111, topPos + 30, 16, 47), 176, 32, 20, 51, TEXTURE)
        );
    }

    @Override
    protected void gatherAdditionalTooltips(int mouseX, int mouseY, Consumer<Component> addLine, Consumer<Component> addGray)
    {
        super.gatherAdditionalTooltips(mouseX, mouseY, addLine, addGray);
        if(distributeButton.isHoveredOrFocused())
            addLine.accept(new TranslatableComponent("advancedtfctech.gui.distribute"));
    }

    @Override
    protected void drawContainerBackgroundPre(@Nonnull PoseStack transform, float f, int mx, int my)
    {
        for (MultiblockProcess<?> process : tile.processQueue)
            if (process instanceof MultiblockProcessInMachine<?> inMachine)
            {
                float mod = process.processTick / (float) process.getMaxTicks(tile.getLevel());
                int slot = inMachine.getInputSlots()[0];
                int h = (int) Math.max(1, mod * 16);
                this.blit(transform, leftPos + 33 + slot % 4 * 21, topPos + 13 + slot / 4 * 18 + (16 - h), 176, 16 - h, 2, h);
            }
    }

    @Override
    public void init()
    {
        super.init();
        distributeButton = new GuiButtonIE(leftPos + 111, topPos + 9, 16, 16, TextComponent.EMPTY, TEXTURE, 179, 0, button -> {
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
