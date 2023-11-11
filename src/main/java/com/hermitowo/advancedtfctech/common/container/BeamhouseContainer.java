package com.hermitowo.advancedtfctech.common.container;

import java.util.List;
import blusunrize.immersiveengineering.api.energy.IMutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericContainerData;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericDataSerializers;
import com.hermitowo.advancedtfctech.common.multiblocks.process.ATTMultiblockProcess;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.BeamhouseLogic;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import javax.annotation.Nonnull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BeamhouseContainer extends ATTContainerMenu
{
    public final IMutableEnergyStorage energy;
    public final FluidTank tank;
    public final GetterAndSetter<List<ProcessSlot>> processes;

    public static BeamhouseContainer makeServer(MenuType<?> type, int id, Inventory playerInventory, MultiblockMenuContext<BeamhouseLogic.State> ctx)
    {
        final BeamhouseLogic.State state = ctx.mbContext().getState();
        return new BeamhouseContainer(
            multiblockCtx(type, id, ctx),
            playerInventory,
            state.getInventory(),
            state.getEnergy(),
            state.getTank(),
            GetterAndSetter.getterOnly(() -> state.getProcessQueue().stream()
                .filter(p -> p instanceof ATTMultiblockProcess.ProcessWithItemStackProvider<BeamhouseRecipe>)
                .map(p -> ProcessSlot.fromCtx((ATTMultiblockProcess.ProcessWithItemStackProvider<BeamhouseRecipe>) p, ctx.mbContext().getLevel().getRawLevel()))
                .toList()
            )
        );
    }

    public static BeamhouseContainer makeClient(MenuType<?> type, int id, Inventory playerInventory)
    {
        return new BeamhouseContainer(
            clientCtx(type, id),
            playerInventory,
            new ItemStackHandler(BeamhouseLogic.NUM_SLOTS),
            new MutableEnergyStorage(BeamhouseLogic.ENERGY_CAPACITY),
            new FluidTank(BeamhouseLogic.TANK_CAPACITY),
            GetterAndSetter.standalone(List.of())
        );
    }

    public BeamhouseContainer(MenuContext ctx, Inventory playerInventory, IItemHandler inv, IMutableEnergyStorage energy, FluidTank tank, GetterAndSetter<List<ProcessSlot>> processes)
    {
        super(ctx);
        this.energy = energy;
        this.tank = tank;
        this.processes = processes;

        for (int i = 0; i < 12; i++)
        {
            this.addSlot(new SlotItemHandler(inv, i, 16 + 21 * (i % 4), 13 + 18 * (i / 4))
            {
                @Override
                public boolean mayPlace(@Nonnull ItemStack stack)
                {
                    return BeamhouseRecipe.isValidRecipeInput(playerInventory.player.level(), stack);
                }
            });
        }
        for (int i = 0; i < 3; i++)
            this.addSlot(new IESlot.NewOutput(inv, i + 12, 46 + 18 * i, 72));
        this.addSlot(new IESlot.NewFluidContainer(inv, 15, 133, 28, IESlot.NewFluidContainer.Filter.ANY));
        this.addSlot(new IESlot.NewOutput(inv, 16, 133, 64));
        ownSlotCount = 17;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 103 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 161));
        addGenericData(GenericContainerData.energy(energy));
        addGenericData(GenericContainerData.fluid(tank));
        addGenericData(new ATTGenericContainerData<>(ATTGenericDataSerializers.BEAMHOUSE_PROCESS_SLOTS, processes));
    }

    public record ProcessSlot(int slot, int processStep)
    {
        public static ProcessSlot fromCtx(ATTMultiblockProcess.ProcessWithItemStackProvider<BeamhouseRecipe> process, Level level)
        {
            float mod = process.processTick / (float) process.getMaxTicks(level);
            int slot = process.getInputSlots()[0];
            int h = (int) Math.max(1, mod * 16);
            return new ProcessSlot(slot, h);
        }

        public static ProcessSlot from(FriendlyByteBuf buffer)
        {
            return new ProcessSlot(buffer.readByte(), buffer.readByte());
        }

        public static void writeTo(FriendlyByteBuf out, ProcessSlot slot)
        {
            out.writeByte(slot.slot).writeByte(slot.processStep);
        }
    }
}
