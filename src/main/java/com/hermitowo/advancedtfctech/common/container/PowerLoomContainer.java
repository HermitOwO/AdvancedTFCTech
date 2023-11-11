package com.hermitowo.advancedtfctech.common.container;

import java.util.List;
import blusunrize.immersiveengineering.api.energy.IMutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericContainerData;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericDataSerializers;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.PowerLoomLogic;
import com.hermitowo.advancedtfctech.common.multiblocks.process.ATTMultiblockProcess;
import com.hermitowo.advancedtfctech.common.recipes.PowerLoomRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import static java.lang.Math.*;

public class PowerLoomContainer extends ATTContainerMenu
{
    public final IMutableEnergyStorage energy;
    public final GetterAndSetter<List<ProcessSlot>> processes;

    public static PowerLoomContainer makeServer(MenuType<?> type, int id, Inventory playerInventory, MultiblockMenuContext<PowerLoomLogic.State> ctx)
    {
        final PowerLoomLogic.State state = ctx.mbContext().getState();
        return new PowerLoomContainer(
            multiblockCtx(type, id, ctx),
            playerInventory,
            state.getInventory(),
            state.getEnergy(),
            GetterAndSetter.getterOnly(() -> state.getProcessQueue().stream()
                .filter(p -> p instanceof ATTMultiblockProcess<PowerLoomRecipe>)
                .map(p -> ProcessSlot.fromCtx((ATTMultiblockProcess<PowerLoomRecipe>) p, ctx.mbContext().getLevel().getRawLevel()))
                .toList()
            )
        );
    }

    public static PowerLoomContainer makeClient(MenuType<?> type, int id, Inventory playerInventory)
    {
        return new PowerLoomContainer(
            clientCtx(type, id),
            playerInventory,
            new ItemStackHandler(PowerLoomLogic.NUM_SLOTS),
            new MutableEnergyStorage(PowerLoomLogic.ENERGY_CAPACITY),
            GetterAndSetter.standalone(List.of())
        );
    }

    public PowerLoomContainer(MenuContext ctx, Inventory playerInventory, IItemHandler inv, IMutableEnergyStorage energy, GetterAndSetter<List<ProcessSlot>> processes)
    {
        super(ctx);
        this.energy = energy;
        this.processes = processes;

        // Pirn Input
        for (int i = 0; i < 8; i++)
            this.addSlot(new IESlot.NewOutput(inv, i, (int) round(cos(PI * i / 4) * 30) + 80, (int) round(sin(PI * i / 4) * 30) + 70));

        // Weave Input
        for (int i = 0; i < 3; i++)
            this.addSlot(new IESlot.NewOutput(inv, i + 8, 62 + 18 * i, 10));

        // Secondary Weave Input
        this.addSlot(new IESlot.NewOutput(inv, 11, 10, 10));

        // Output
        this.addSlot(new IESlot.NewOutput(inv, 12, 132, 98));

        ownSlotCount = 13;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 126 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 184));
        addGenericData(GenericContainerData.energy(energy));
        addGenericData(new ATTGenericContainerData<>(ATTGenericDataSerializers.POWER_LOOM_PROCESS_SLOTS, processes));
    }

    public record ProcessSlot(int processStep)
    {
        public static ProcessSlot fromCtx(ATTMultiblockProcess<PowerLoomRecipe> process, Level level)
        {
            float mod = process.processTick / (float) process.getMaxTicks(level);
            int h = (int) Math.max(1, mod * 16);
            return new ProcessSlot(h);
        }

        public static ProcessSlot from(FriendlyByteBuf buffer)
        {
            return new ProcessSlot(buffer.readByte());
        }

        public static void writeTo(FriendlyByteBuf out, ProcessSlot slot)
        {
            out.writeByte(slot.processStep);
        }
    }
}