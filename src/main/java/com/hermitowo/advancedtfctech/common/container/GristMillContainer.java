package com.hermitowo.advancedtfctech.common.container;

import java.util.List;
import blusunrize.immersiveengineering.api.energy.IMutableEnergyStorage;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.common.gui.IESlot;
import blusunrize.immersiveengineering.common.gui.sync.GenericContainerData;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericContainerData;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericDataSerializers;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.GristMillLogic;
import com.hermitowo.advancedtfctech.common.multiblocks.process.ATTMultiblockProcess;
import com.hermitowo.advancedtfctech.common.recipes.GristMillRecipe;
import javax.annotation.Nonnull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class GristMillContainer extends ATTContainerMenu
{
    public final IMutableEnergyStorage energy;
    public final GetterAndSetter<List<ProcessSlot>> processes;

    public static GristMillContainer makeServer(MenuType<?> type, int id, Inventory playerInventory, MultiblockMenuContext<GristMillLogic.State> ctx)
    {
        final GristMillLogic.State state = ctx.mbContext().getState();
        return new GristMillContainer(
            multiblockCtx(type, id, ctx),
            playerInventory,
            state.getInventory(),
            state.getEnergy(),
            GetterAndSetter.getterOnly(() -> state.getProcessQueue().stream()
                .filter(p -> p instanceof ATTMultiblockProcess.ProcessWithItemStackProvider<GristMillRecipe>)
                .map(p -> ProcessSlot.fromCtx((ATTMultiblockProcess.ProcessWithItemStackProvider<GristMillRecipe>) p, ctx.mbContext().getLevel().getRawLevel()))
                .toList()
            )
        );
    }

    public static GristMillContainer makeClient(MenuType<?> type, int id, Inventory playerInventory)
    {
        return new GristMillContainer(
            clientCtx(type, id),
            playerInventory,
            new ItemStackHandler(GristMillLogic.NUM_SLOTS),
            new MutableEnergyStorage(GristMillLogic.ENERGY_CAPACITY),
            GetterAndSetter.standalone(List.of())
        );
    }

    public GristMillContainer(MenuContext ctx, Inventory playerInventory, IItemHandler inv, IMutableEnergyStorage energy, GetterAndSetter<List<ProcessSlot>> processes)
    {
        super(ctx);
        this.energy = energy;
        this.processes = processes;

        for (int i = 0; i < 6; i++)
        {
            this.addSlot(new SlotItemHandler(inv, i, 62 + 18 * (i % 3), 16 + 18 * (i / 3))
            {
                @Override
                public boolean mayPlace(@Nonnull ItemStack stack)
                {
                    return GristMillRecipe.findRecipe(playerInventory.player.level(), stack) != null;
                }
            });
        }
        for (int i = 0; i < 6; i++)
            this.addSlot(new IESlot.NewOutput(inv, i + 6, 62 + 18 * (i % 3), 74 + 18 * (i / 3)));
        ownSlotCount = 12;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 126 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 184));
        addGenericData(GenericContainerData.energy(energy));
        addGenericData(new ATTGenericContainerData<>(ATTGenericDataSerializers.GRIST_MILL_PROCESS_SLOTS, processes));
    }

    public record ProcessSlot(int processStep)
    {
        public static ProcessSlot fromCtx(ATTMultiblockProcess.ProcessWithItemStackProvider<GristMillRecipe> process, Level level)
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
