package com.hermitowo.advancedtfctech.common.container;

import com.hermitowo.advancedtfctech.common.blockentities.GristMillBlockEntity;
import com.hermitowo.advancedtfctech.common.multiblocks.GristMillMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class GristMillContainer extends MultiblockAwareGuiContainer<GristMillBlockEntity>
{
    public GristMillContainer(MenuType<?> type, int id, Inventory playerInventory, final GristMillBlockEntity blockentity)
    {
        super(type, blockentity, id, GristMillMultiblock.INSTANCE);

        for (int i = 0; i < 6; i++)
        {
            addSlot(new ATTSlot.GristMillInput(this, this.inv, i, 62 + 18 * (i % 3), 16 + 18 * (i / 3), blockentity.getLevel()));
        }
        for (int i = 0; i < 6; i++)
        {
            addSlot(new ATTSlot.ItemOutput(this, this.inv, i + 6, 62 + 18 * (i % 3), 74 + 18 * (i / 3)));
        }

        slotCount = 12;

        addPlayerInventorySlots(playerInventory, 8, 126);
        addPlayerHotbarSlots(playerInventory, 8, 184);
    }
}
