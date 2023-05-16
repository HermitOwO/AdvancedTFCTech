package com.hermitowo.advancedtfctech.common.container;

import com.hermitowo.advancedtfctech.common.blockentities.PowerLoomBlockEntity;
import com.hermitowo.advancedtfctech.common.multiblocks.PowerLoomMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import static java.lang.Math.*;

public class PowerLoomContainer extends MultiblockAwareGuiContainer<PowerLoomBlockEntity>
{
    public PowerLoomContainer(MenuType<?> type, int id, Inventory playerInventory, final PowerLoomBlockEntity blockentity)
    {
        super(type, blockentity, id, PowerLoomMultiblock.INSTANCE);

        for (int i = 0; i < 8; i++)
        {
            addSlot(new ATTSlot.PirnInput(this, this.inv, i, (int) round(cos(PI * i / 4) * 30) + 80, (int) round(sin(PI * i / 4) * 30) + 70, blockentity.getLevel()));
        }
        for (int i = 0; i < 3; i++)
        {
            addSlot(new ATTSlot.WeaveInput(this, this.inv, i + 8, 62 + 18 * i, 10, blockentity.getLevel()));
        }
        addSlot(new ATTSlot.SecondaryWeaveInput(this, this.inv, 11, 10, 10, blockentity.getLevel()));
        addSlot(new ATTSlot.ItemOutput(this, this.inv, 12, 132, 98));
        addSlot(new ATTSlot.ItemOutput(this, this.inv, 13, 150, 98));

        slotCount = 14;

        addPlayerInventorySlots(playerInventory, 8, 126);
        addPlayerHotbarSlots(playerInventory, 8, 184);
    }
}