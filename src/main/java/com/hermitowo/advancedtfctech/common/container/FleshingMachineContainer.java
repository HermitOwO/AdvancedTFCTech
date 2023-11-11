package com.hermitowo.advancedtfctech.common.container;

import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import static com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity.*;

public class FleshingMachineContainer extends IEContainerMenu
{
    public final EnergyStorage energy;

    public static FleshingMachineContainer makeServer(MenuType<?> type, int id, Inventory playerInventory, FleshingMachineBlockEntity be)
    {
        return new FleshingMachineContainer(blockCtx(type, id, be), playerInventory, new ItemStackHandler(be.getInventory()), be.energyStorage);
    }

    public static FleshingMachineContainer makeClient(MenuType<?> type, int id, Inventory playerInventory)
    {
        return new FleshingMachineContainer(clientCtx(type, id), playerInventory, new ItemStackHandler(NUM_SLOTS), new MutableEnergyStorage(ENERGY_CAPACITY));
    }

    public FleshingMachineContainer(MenuContext ctx, Inventory playerInventory, IItemHandler inv, MutableEnergyStorage energy)
    {
        super(ctx);
        this.energy = energy;

        this.addSlot(new IESlot.NewOutput(inv, 0, 62, 10));
        this.addSlot(new IESlot.NewOutput(inv, 1, 10, 10));

        ownSlotCount = 2;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 126 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 184));
        addGenericData(GenericContainerData.energy(energy));
    }
}
