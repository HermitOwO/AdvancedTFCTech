package com.hermitowo.advancedtfctech.common.container;

import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import com.hermitowo.advancedtfctech.common.blockentities.PowerLoomBlockEntity;
import com.hermitowo.advancedtfctech.common.multiblocks.PowerLoomMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import static java.lang.Math.*;

public class PowerLoomContainer extends MultiblockAwareGuiContainer<PowerLoomBlockEntity>
{
    public PowerLoomContainer(MenuType<?> type, int id, Inventory playerInventory, final PowerLoomBlockEntity blockEntity)
    {
        super(type, blockEntity, id, PowerLoomMultiblock.INSTANCE);

        // Pirn Input
        for (int i = 0; i < 8; i++)
            addSlot(new ATTSlot.NotPlaceable(this, this.inv, i, (int) round(cos(PI * i / 4) * 30) + 80, (int) round(sin(PI * i / 4) * 30) + 70));

        // Weave Input
        for (int i = 0; i < 3; i++)
            addSlot(new ATTSlot.NotPlaceable(this, this.inv, i + 8, 62 + 18 * i, 10));

        // Secondary Weave Input
        addSlot(new ATTSlot.NotPlaceable(this, this.inv, 11, 10, 10));
        addSlot(new ATTSlot.NotPlaceable(this, this.inv, 12, 132, 98));

        slotCount = 13;

        addPlayerInventorySlots(playerInventory, 8, 126);
        addPlayerHotbarSlots(playerInventory, 8, 184);

        addGenericData(GenericContainerData.energy(blockEntity.energyStorage));
    }
}