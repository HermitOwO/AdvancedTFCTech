package com.hermitowo.advancedtfctech.common.container;

import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import com.hermitowo.advancedtfctech.common.blockentities.BeamhouseBlockEntity;
import com.hermitowo.advancedtfctech.common.multiblocks.BeamhouseMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class BeamhouseContainer extends MultiblockAwareGuiContainer<BeamhouseBlockEntity>
{
    public BeamhouseContainer(MenuType<?> type, int id, Inventory playerInventory, final BeamhouseBlockEntity blockEntity)
    {
        super(type, blockEntity, id, BeamhouseMultiblock.INSTANCE);

        for (int i = 0; i < 12; i++)
            addSlot(new ATTSlot.BeamhouseInput(this, this.inv, i, 16 + 21 * (i % 4), 13 + 18 * (i / 4), blockEntity.getLevel()));

        for (int i = 0; i < 3; i++)
            addSlot(new ATTSlot.NotPlaceable(this, this.inv, i + 12, 46 + 18 * i, 72));

        addSlot(new IESlot.FluidContainer(this, this.inv, 15, 133, 28, 0));
        addSlot(new ATTSlot.NotPlaceable(this, this.inv, 16, 133, 64));

        slotCount = 17;

        addPlayerInventorySlots(playerInventory, 8, 103);
        addPlayerHotbarSlots(playerInventory, 8, 161);

        addGenericData(GenericContainerData.energy(blockEntity.energyStorage));
        addGenericData(GenericContainerData.fluid(blockEntity.tank));
    }
}
