package com.hermitowo.advancedtfctech.common.container;

import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class FleshingMachineContainer extends IEBaseContainer<FleshingMachineBlockEntity>
{
    public FleshingMachineContainer(MenuType<?> type, int id, Inventory playerInventory, final FleshingMachineBlockEntity blockEntity)
    {
        super(type, blockEntity, id);

        addSlot(new ATTSlot.NotPlaceable(this, this.inv, 0, 62, 10));
        addSlot(new ATTSlot.NotPlaceable(this, this.inv, 1, 10, 10));

        slotCount = 2;

        addPlayerInventorySlots(playerInventory);
        addPlayerHotbarSlots(playerInventory);

        addGenericData(GenericContainerData.energy(blockEntity.energyStorage));
    }

    private void addPlayerInventorySlots(Inventory playerInventory)
    {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 126 + i * 18));
    }

    private void addPlayerHotbarSlots(Inventory playerInventory)
    {
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 184));
    }
}
