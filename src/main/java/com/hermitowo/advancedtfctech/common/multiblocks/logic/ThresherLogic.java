package com.hermitowo.advancedtfctech.common.multiblocks.logic;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.CapabilityPosition;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MBInventoryUtils;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.StoredCapability;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.sound.MultiblockSound;
import com.hermitowo.advancedtfctech.client.ATTSounds;
import com.hermitowo.advancedtfctech.common.multiblocks.process.ATTMultiblockProcess;
import com.hermitowo.advancedtfctech.common.multiblocks.process.ATTProcessContext;
import com.hermitowo.advancedtfctech.common.multiblocks.shapes.ThresherShapes;
import com.hermitowo.advancedtfctech.common.recipes.ThresherRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class ThresherLogic implements IMultiblockLogic<ThresherLogic.State>, IServerTickableComponent<ThresherLogic.State>, IClientTickableComponent<ThresherLogic.State>
{
    public static final BlockPos REDSTONE_POS = new BlockPos(0, 1, 1);
    private static final CapabilityPosition ENERGY_POS = new CapabilityPosition(2, 1, 1, RelativeBlockFace.LEFT);

    public static final int FIRST_IN_SLOT = 0;
    public static final int IN_SLOT_COUNT = 6;
    public static final int FIRST_OUT_SLOT = 6;
    public static final int OUT_SLOT_COUNT = 6;
    private static final MultiblockFace MAIN_OUT_POS = new MultiblockFace(1, 0, -1, RelativeBlockFace.FRONT);
    private static final MultiblockFace SECONDARY_OUT_POS = new MultiblockFace(1, 1, 3, RelativeBlockFace.FRONT);
    private static final CapabilityPosition IN_CAP = new CapabilityPosition(1, 2, 1, RelativeBlockFace.UP);
    private static final CapabilityPosition MAIN_OUT_CAP = CapabilityPosition.opposing(MAIN_OUT_POS);
    private static final int[] OUTPUT_SLOTS = new int[] {6, 7, 8, 9, 10, 11};
    public static final int NUM_SLOTS = 12;
    public static final int ENERGY_CAPACITY = 32000;

    @Override
    public void tickServer(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        final boolean active = state.processor.tickServer(state, context.getLevel(), state.rsState.isEnabled(context));
        if (active != state.active)
        {
            state.active = active;
            context.requestMasterBESync();
        }
        enqueueProcesses(state, context.getLevel().getRawLevel());
        if (context.getLevel().shouldTickModulo(8))
        {
            ATTMultiblockLogicHelper.sort(state, FIRST_OUT_SLOT, OUT_SLOT_COUNT);
            ATTMultiblockLogicHelper.handleItemOutput(state, state.output, OUTPUT_SLOTS);
        }
    }

    private void enqueueProcesses(State state, Level level)
    {
        if (state.energy.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize())
            return;
        final int[] usedInvSlots = new int[IN_SLOT_COUNT];
        for (MultiblockProcess<?, ?> process : state.processor.getQueue())
            if (process instanceof MultiblockProcessInMachine)
                for (int i : ((MultiblockProcessInMachine<?>) process).getInputSlots())
                    usedInvSlots[i]++;

        for (int slot = FIRST_IN_SLOT; slot < IN_SLOT_COUNT; slot++)
        {
            ItemStack stack = state.inventory.getStackInSlot(slot);
            if (stack.getCount() <= usedInvSlots[slot])
                continue;
            stack = stack.copy();
            stack.shrink(usedInvSlots[slot]);
            ThresherRecipe recipe = ThresherRecipe.findRecipe(level, stack);
            if (recipe != null)
                state.processor.addProcessToQueue(new ATTMultiblockProcess.ProcessWithItemStackProvider<>(recipe, slot), level, false);
        }
    }

    @Override
    public void tickClient(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        if (!state.isPlayingSound.getAsBoolean())
        {
            final Vec3 soundPos = context.getLevel().toAbsolute(new Vec3(1.5, 1.5, 1.5));
            state.isPlayingSound = MultiblockSound.startSound(
                () -> state.active, context.isValid(), soundPos, ATTSounds.THRESHER
            );
        }
    }

    @Override
    public State createInitialState(IInitialMultiblockContext<State> capabilitySource)
    {
        return new State(capabilitySource);
    }

    @Override
    public <T> LazyOptional<T> getCapability(IMultiblockContext<State> ctx, CapabilityPosition position, Capability<T> cap)
    {
        final State state = ctx.getState();
        if (cap == ForgeCapabilities.ENERGY && ENERGY_POS.equalsOrNullFace(position))
            return state.energyCap.cast(ctx);
        if (cap == ForgeCapabilities.ITEM_HANDLER)
        {
            if (MAIN_OUT_CAP.equals(position))
                return state.outputHandler.cast(ctx);
            if (IN_CAP.equals(position))
                return state.insertionHandler.cast(ctx);
        }
        return LazyOptional.empty();
    }

    @Override
    public void dropExtraItems(State state, Consumer<ItemStack> drop)
    {
        MBInventoryUtils.dropItems(state.inventory, drop);
    }

    @Override
    public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType)
    {
        return ThresherShapes.SHAPE_GETTER;
    }

    public static class State implements IMultiblockState, ATTProcessContext<ThresherRecipe>
    {
        private final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final MultiblockProcessor<ThresherRecipe, ATTProcessContext<ThresherRecipe>> processor;
        public final SlotwiseItemHandler inventory;
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        private final CapabilityReference<IItemHandler> output;
        private final DroppingMultiblockOutput secondaryOutput;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final StoredCapability<IItemHandler> insertionHandler;
        private final StoredCapability<IItemHandler> outputHandler;

        // Client
        private boolean active;
        private BooleanSupplier isPlayingSound = () -> false;

        public State(IInitialMultiblockContext<State> ctx)
        {
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
            this.inventory = SlotwiseItemHandler.makeWithGroups(List.of(
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.input(stack -> ThresherRecipe.findRecipe(getLevel.get(), stack) != null), IN_SLOT_COUNT),
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, OUT_SLOT_COUNT)
            ), markDirty);
            this.processor = new MultiblockProcessor<>(
                1, 0, 1, markDirty, ThresherRecipe.RECIPES::getById
            );
            this.output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, MAIN_OUT_POS);
            this.secondaryOutput = new DroppingMultiblockOutput(SECONDARY_OUT_POS, ctx);
            this.energyCap = new StoredCapability<>(this.energy);
            this.insertionHandler = new StoredCapability<>(new WrappingItemHandler(
                inventory, true, false, new WrappingItemHandler.IntRange(FIRST_IN_SLOT, FIRST_IN_SLOT + IN_SLOT_COUNT)
            ));
            this.outputHandler = new StoredCapability<>(new WrappingItemHandler(
                inventory, false, true, new WrappingItemHandler.IntRange(FIRST_OUT_SLOT, FIRST_OUT_SLOT + OUT_SLOT_COUNT)
            ));
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt)
        {
            nbt.put("energy", energy.serializeNBT());
            nbt.put("processor", processor.toNBT());
            nbt.put("inventory", inventory.serializeNBT());
        }

        @Override
        public void readSaveNBT(CompoundTag nbt)
        {
            energy.deserializeNBT(nbt.get("energy"));
            processor.fromNBT(nbt.get("processor"), ATTMultiblockProcess.ProcessWithItemStackProvider::new);
            inventory.deserializeNBT(nbt.getCompound("inventory"));
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            nbt.putBoolean("active", active);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            active = nbt.getBoolean("active");
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }

        @Override
        public IItemHandlerModifiable getInventory()
        {
            return inventory.getRawHandler();
        }

        @Override
        public int[] getOutputSlots()
        {
            return OUTPUT_SLOTS;
        }

        @Override
        public void doProcessOutput(ItemStack output, IMultiblockLevel level)
        {
            this.secondaryOutput.insertOrDrop(output, level);
        }

        public List<MultiblockProcess<ThresherRecipe, ATTProcessContext<ThresherRecipe>>> getProcessQueue()
        {
            return processor.getQueue();
        }
    }
}
