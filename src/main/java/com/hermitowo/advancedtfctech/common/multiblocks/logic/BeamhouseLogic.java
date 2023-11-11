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
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.fluids.ArrayFluidHandler;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import blusunrize.immersiveengineering.common.util.sound.MultiblockSound;
import com.hermitowo.advancedtfctech.client.ATTSounds;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.multiblocks.process.ATTMultiblockProcess;
import com.hermitowo.advancedtfctech.common.multiblocks.process.ATTProcessContext;
import com.hermitowo.advancedtfctech.common.multiblocks.shapes.BeamhouseShapes;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import com.hermitowo.advancedtfctech.util.FluidHelper;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class BeamhouseLogic implements IMultiblockLogic<BeamhouseLogic.State>, IServerTickableComponent<BeamhouseLogic.State>, IClientTickableComponent<BeamhouseLogic.State>
{
    public static final BlockPos REDSTONE_POS = new BlockPos(3, 1, 2);
    private static final CapabilityPosition ENERGY_POS = new CapabilityPosition(0, 1, 3, RelativeBlockFace.UP);

    public static final int FIRST_IN_SLOT = 0;
    public static final int IN_SLOT_COUNT = 12;
    public static final int FIRST_OUT_SLOT = 12;
    public static final int OUT_SLOT_COUNT = 3;
    public static final int CONTAINER_IN_SLOT = 15;
    public static final int CONTAINER_OUT_SLOT = 16;
    private static final MultiblockFace IN_POS = new MultiblockFace(4, 0, 1, RelativeBlockFace.RIGHT);
    private static final MultiblockFace OUT_POS = new MultiblockFace(3, 0, -1, RelativeBlockFace.FRONT);
    private static final CapabilityPosition FLUID_CAP = new CapabilityPosition(2, 0, 3, RelativeBlockFace.BACK);
    private static final CapabilityPosition IN_CAP = CapabilityPosition.opposing(IN_POS);
    private static final CapabilityPosition OUT_CAP = CapabilityPosition.opposing(OUT_POS);
    private static final int[] OUTPUT_SLOTS = new int[] {12, 13, 14};
    public static final int NUM_SLOTS = 17;
    public static final int ENERGY_CAPACITY = 32000;
    public static final int TANK_CAPACITY = 24000;

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
            ATTMultiblockLogicHelper.handleItemOutput(state, state.output, OUTPUT_SLOTS);
        if (FluidHelper.drainFluidContainer(state.inventory, state.tank, CONTAINER_IN_SLOT, CONTAINER_OUT_SLOT))
            context.requestMasterBESync();
    }

    private void enqueueProcesses(State state, Level level)
    {
        Int2IntOpenHashMap usedInvSlots = new Int2IntOpenHashMap();
        for (MultiblockProcess<BeamhouseRecipe, ATTProcessContext<BeamhouseRecipe>> process : state.processor.getQueue())
        {
            if (process instanceof ATTMultiblockProcess.ProcessWithItemStackProvider<BeamhouseRecipe> beamhouseProcess)
            {
                int[] inputSlots = beamhouseProcess.getInputSlots();
                int[] inputAmounts = beamhouseProcess.getInputAmounts();
                if (inputAmounts == null)
                    continue;
                for (int i = 0; i < inputSlots.length; i++)
                    if (inputAmounts[i] > 0)
                        usedInvSlots.addTo(inputSlots[i], inputAmounts[i]);
            }
        }

        for (int slot = FIRST_IN_SLOT; slot < FIRST_IN_SLOT + IN_SLOT_COUNT; slot++)
        {
            if (state.tank.isEmpty())
                continue;
            if (usedInvSlots.containsKey(slot))
                continue;
            ItemStack stack = state.inventory.getStackInSlot(slot);
            if (stack.isEmpty())
                continue;
            BeamhouseRecipe recipe = BeamhouseRecipe.findRecipe(level, stack, state.tank.getFluid());
            if (recipe == null)
                continue;
            int fluidAmount = 0;
            for (var processInQueue : state.getProcessQueue())
            {
                BeamhouseRecipe recipeInQueue = processInQueue.getRecipe(level);
                if (recipeInQueue != null)
                    fluidAmount += recipeInQueue.fluidInput.getAmount();
            }
            fluidAmount += recipe.fluidInput.getAmount();
            if (state.tank.getFluidAmount() >= fluidAmount)
            {
                ATTMultiblockProcess<BeamhouseRecipe> process = new ATTMultiblockProcess.ProcessWithItemStackProvider<>(recipe, slot).setInputTanks(0);
                if (state.processor.addProcessToQueue(process, level, false))
                    process.setInputAmounts(recipe.input.getCount());
            }
        }
    }

    @Override
    public void tickClient(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        if (state.active)
            state.barrelAngle = (state.barrelAngle + 18) % 360;
        if (!state.isPlayingSound.getAsBoolean())
        {
            final Vec3 soundPos = context.getLevel().toAbsolute(new Vec3(1.5, 1.5, 1.5));
            state.isPlayingSound = MultiblockSound.startSound(
                () -> state.active, context.isValid(), soundPos, ATTSounds.BEAMHOUSE
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
        if (cap == ForgeCapabilities.FLUID_HANDLER && FLUID_CAP.equalsOrNullFace(position))
            return state.fluidInputHandler.cast(ctx);
        if (cap == ForgeCapabilities.ITEM_HANDLER)
        {
            if (OUT_CAP.equals(position))
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
        return BeamhouseShapes.SHAPE_GETTER;
    }

    @Override
    public InteractionResult click(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient)
    {
        if (isClient)
            return InteractionResult.SUCCESS;

        final State state = ctx.getState();
        if (FLUID_CAP.posInMultiblock().equals(posInMultiblock))
        {
            if (FluidHelper.interactWithFluidHandler(player, hand, state.tank, state.getProcessQueue().isEmpty()))
            {
                ctx.markMasterDirty();
                ctx.requestMasterBESync();
            }
            else
                player.openMenu(ATTContainerTypes.BEAMHOUSE.provide(ctx, posInMultiblock));
        }
        else
            player.openMenu(ATTContainerTypes.BEAMHOUSE.provide(ctx, posInMultiblock));
        return InteractionResult.SUCCESS;
    }

    public static class State implements IMultiblockState, ATTProcessContext<BeamhouseRecipe>
    {
        private final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final MultiblockProcessor<BeamhouseRecipe, ATTProcessContext<BeamhouseRecipe>> processor;
        public final SlotwiseItemHandler inventory;
        public final FluidTank tank = new FluidTank(TANK_CAPACITY);
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        private final CapabilityReference<IItemHandler> output;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final StoredCapability<IItemHandler> insertionHandler;
        private final StoredCapability<IItemHandler> outputHandler;
        private final StoredCapability<IFluidHandler> fluidInputHandler;

        // Client
        private boolean active;
        public float barrelAngle = 0;
        private BooleanSupplier isPlayingSound = () -> false;

        public State(IInitialMultiblockContext<State> ctx)
        {
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
            this.inventory = SlotwiseItemHandler.makeWithGroups(List.of(
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.input(stack -> BeamhouseRecipe.isValidRecipeInput(getLevel.get(), stack)), IN_SLOT_COUNT),
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, OUT_SLOT_COUNT),
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.FLUID_INPUT, 1),
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, 1)
            ), markDirty);
            this.processor = new MultiblockProcessor<>(
                12, 0, 12, markDirty, BeamhouseRecipe.RECIPES::getById
            );
            this.output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, OUT_POS);
            this.energyCap = new StoredCapability<>(this.energy);
            this.insertionHandler = new StoredCapability<>(new BeamhouseInputHandler(inventory, markDirty, getLevel));
            this.outputHandler = new StoredCapability<>(new WrappingItemHandler(
                inventory, false, true, new WrappingItemHandler.IntRange(FIRST_OUT_SLOT, FIRST_OUT_SLOT + OUT_SLOT_COUNT)
            ));
            this.fluidInputHandler = new StoredCapability<>(ArrayFluidHandler.fillOnly(tank, markDirty));
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt)
        {
            nbt.put("energy", energy.serializeNBT());
            nbt.put("processor", processor.toNBT());
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
        }

        @Override
        public void readSaveNBT(CompoundTag nbt)
        {
            energy.deserializeNBT(nbt.get("energy"));
            processor.fromNBT(nbt.get("processor"), ATTMultiblockProcess.ProcessWithItemStackProvider::new);
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            tank.readFromNBT(nbt.getCompound("tank"));
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
        public IFluidTank[] getInternalTanks()
        {
            return new IFluidTank[] {tank};
        }

        @Override
        public int[] getOutputSlots()
        {
            return OUTPUT_SLOTS;
        }

        public FluidTank getTank()
        {
            return tank;
        }

        public boolean shouldRenderAsActive()
        {
            return active;
        }

        public List<MultiblockProcess<BeamhouseRecipe, ATTProcessContext<BeamhouseRecipe>>> getProcessQueue()
        {
            return processor.getQueue();
        }
    }
}
