package com.hermitowo.advancedtfctech.common.container;

import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import com.hermitowo.advancedtfctech.common.blockentities.ThresherBlockEntity;
import com.hermitowo.advancedtfctech.common.multiblocks.ThresherMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ThresherContainer extends MultiblockAwareGuiContainer<ThresherBlockEntity>
{
    public ThresherContainer(MenuType<?> type, int id, Inventory playerInventory, final ThresherBlockEntity blockEntity)
    {
        super(type, blockEntity, id, ThresherMultiblock.INSTANCE);

        for (int i = 0; i < 6; i++)
            addSlot(new ATTSlot.ThresherInput(this, this.inv, i, 62 + 18 * (i % 3), 16 + 18 * (i / 3), blockEntity.getLevel()));

        for (int i = 0; i < 6; i++)
            addSlot(new ATTSlot.NotPlaceable(this, this.inv, i + 6, 62 + 18 * (i % 3), 74 + 18 * (i / 3)));

        slotCount = 12;

        addPlayerInventorySlots(playerInventory, 8, 126);
        addPlayerHotbarSlots(playerInventory, 8, 184);

        addGenericData(GenericContainerData.energy(blockEntity.energyStorage));
    }
}
