package com.hermitowo.advancedtfctech.common.multiblocks.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessor;
import blusunrize.immersiveengineering.common.util.DroppingMultiblockOutput;
import blusunrize.immersiveengineering.common.util.inventory.SlotwiseItemHandler;
import blusunrize.immersiveengineering.common.util.inventory.WrappingItemHandler;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.container.PowerLoomInventory;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.multiblocks.process.ATTMultiblockProcess;
import com.hermitowo.advancedtfctech.common.multiblocks.process.ATTProcessContext;
import com.hermitowo.advancedtfctech.common.multiblocks.shapes.PowerLoomSelectionShapes;
import com.hermitowo.advancedtfctech.common.multiblocks.shapes.PowerLoomShapes;
import com.hermitowo.advancedtfctech.common.recipes.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

public class PowerLoomLogic implements IMultiblockLogic<PowerLoomLogic.State>, IServerTickableComponent<PowerLoomLogic.State>, IClientTickableComponent<PowerLoomLogic.State>
{
    public static final BlockPos REDSTONE_POS = new BlockPos(1, 0, 4);
    private static final CapabilityPosition ENERGY_POS = new CapabilityPosition(1, 1, 0, RelativeBlockFace.UP);

    public static final int FIRST_PIRN_IN_SLOT = 0;
    public static final int PIRN_IN_SLOT_COUNT = 8;
    public static final int FIRST_WEAVE_IN_SLOT = 8;
    public static final int WEAVE_IN_SLOT_COUNT = 3;
    public static final int SECONDARY_WEAVE_IN_SLOT = 11;
    public static final int OUT_SLOT = 12;
    private static final Set<BlockPos> WEAVE_IN_POS = Set.of(
        new BlockPos(2, 0, 1),
        new BlockPos(2, 0, 2),
        new BlockPos(2, 0, 3)
    );
    private static final Set<MultiblockFace> OUT_POS = Set.of(
        new MultiblockFace(-1, 0, 1, RelativeBlockFace.LEFT),
        new MultiblockFace(-1, 0, 2, RelativeBlockFace.LEFT),
        new MultiblockFace(-1, 0, 3, RelativeBlockFace.LEFT)
    );

    private static final MultiblockFace MAIN_OUT_POS = new MultiblockFace(-1, 0, 2, RelativeBlockFace.LEFT);
    private static final MultiblockFace SECONDARY_OUT_POS = new MultiblockFace(3, 0, 4, RelativeBlockFace.LEFT);
    private static final BlockPos PIRN_IN_POS = new BlockPos(1, 1, 4);
    private static final BlockPos SECONDARY_WEAVE_IO_POS = new BlockPos(1, 1, 2);
    private static final Set<CapabilityPosition> OUT_CAP = OUT_POS
        .stream()
        .map(CapabilityPosition::opposing)
        .collect(Collectors.toSet());
    public static final int NUM_SLOTS = 13;
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
            ATTMultiblockLogicHelper.sort(state, FIRST_WEAVE_IN_SLOT, WEAVE_IN_SLOT_COUNT);
            ATTMultiblockLogicHelper.handleItemOutput(state, state.output, state.getOutputSlots());
            context.requestMasterBESync();
        }
    }

    private void enqueueProcesses(State state, Level level)
    {
        if (state.energy.getEnergyStored() <= 0 || state.processor.getQueueSize() >= state.processor.getMaxQueueSize())
            return;
        ItemStack secondaryWeave = state.inventory.getStackInSlot(SECONDARY_WEAVE_IN_SLOT);
        if (secondaryWeave.isEmpty())
            return;
        for (int i = FIRST_PIRN_IN_SLOT; i < FIRST_PIRN_IN_SLOT + PIRN_IN_SLOT_COUNT; i++)
        {
            ItemStack pirn = state.inventory.getStackInSlot(i);
            if (pirn.isEmpty())
                continue;
            for (int j = FIRST_WEAVE_IN_SLOT; j < FIRST_WEAVE_IN_SLOT + WEAVE_IN_SLOT_COUNT; j++)
            {
                ItemStack weave = state.inventory.getStackInSlot(j);
                if (weave.isEmpty())
                    continue;
                PowerLoomRecipe recipe = PowerLoomRecipe.findRecipe(level, pirn, weave);
                if (recipe == null)
                    continue;
                if (!recipe.secondaryInput.test(secondaryWeave))
                    continue;
                ItemStack outputSlot = state.inventory.getStackInSlot(OUT_SLOT);
                ItemStack output = recipe.output.get();
                if (outputSlot.isEmpty() || (ItemHandlerHelper.canItemStacksStack(outputSlot, output) && outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize()))
                {
                    state.processor.addProcessToQueue(new ATTMultiblockProcess<>(recipe, i, j), level, false);
                    state.lastTexture = recipe.inProgressTexture;
                }
            }
        }
    }

    @Override
    public void tickClient(IMultiblockContext<State> context)
    {
        final State state = context.getState();
        if (state.processor.getQueue().isEmpty())
        {
            if (state.rackDispl < 0.65625F)
                state.rackDispl = Math.min(0.65625F, state.rackDispl + 0.046875F);

            if (state.rackSideDispl < 0.25F)
                state.rackSideDispl = Math.min(0.25F, state.rackSideDispl + 0.125F);

            if (state.rack2Displ < 0.4375F)
                state.rack2Displ = Math.min(0.4375F, state.rack2Displ + 0.0875F);

            if (state.longThreadAngle < 19.0F)
                state.longThreadAngle = Math.min(19.0F, state.longThreadAngle + 3.8F);

            if (state.shortThreadAngle < 42.0F)
                state.shortThreadAngle = Math.min(42.0F, state.shortThreadAngle + 8.4F);

            state.pirnAngle = 0;
            state.pirnDisplX = 0;
            state.pirnDisplY = 0;
            state.pirnDisplZ = 0;

            context.requestMasterBESync();
        }

        if (state.active)
        {
            state.rodAngle = (state.rodAngle + 1.75F) % 360;
            context.requestMasterBESync();
        }

        for (MultiblockProcess<PowerLoomRecipe, ?> process : state.processor.getQueue())
        {
            if (state.active)
            {
                int tick = process.processTick;
                int delayedTick = tick - 20;

                if (delayedTick > 0)
                {
                    if (state.rackBool)
                        state.rackDispl = Math.max(0, state.rackDispl - 0.046875F);
                    else
                        state.rackDispl = Math.min(0.65625F, state.rackDispl + 0.046875F);
                    if (state.rackDispl <= 0 && state.rackBool)
                        state.rackBool = false;
                    else if (state.rackDispl >= 0.65625F && !state.rackBool)
                        state.rackBool = true;
                }
                else if (state.rackDispl < 0.65625F)
                    state.rackDispl = Math.min(0.65625F, state.rackDispl + 0.046875F);

                if (delayedTick / 28 >= 1 && delayedTick % 28 <= 4)
                {
                    if (state.rack2Bool)
                    {
                        state.rack2Displ = Math.max(0, state.rack2Displ - 0.0875F);
                        state.longThreadAngle = Math.max(0, state.longThreadAngle - 3.8F);
                        state.shortThreadAngle = Math.max(0, state.shortThreadAngle - 8.4F);
                    }
                    else
                    {
                        state.rack2Displ = Math.min(0.4375F, state.rack2Displ + 0.0875F);
                        state.longThreadAngle = Math.min(19.0F, state.longThreadAngle + 3.8F);
                        state.shortThreadAngle = Math.min(42.0F, state.shortThreadAngle + 8.4F);
                    }
                    if (state.rack2Displ <= 0 && state.rack2Bool)
                        state.rack2Bool = false;
                    else if (state.rack2Displ >= 0.4375F && !state.rack2Bool)
                        state.rack2Bool = true;
                }

                if (tick <= 15)
                    state.pirnAngle = 3.0F * tick;
                else
                    state.pirnAngle = 45.0F;

                if (tick > 15 && tick <= 20)
                {
                    state.pirnDisplX = 0.02965F * (tick - 15);
                    state.pirnDisplY = 0.046875F * (tick - 15);
                }
                else
                {
                    state.pirnDisplX = 0.14825F;
                    state.pirnDisplY = 0.234375F;
                }

                if (delayedTick % 56 > 10 && delayedTick % 56 <= 18)
                {
                    if (state.pirnBool)
                        state.pirnDisplZ = Math.max(0, state.pirnDisplZ - 0.703125F);
                    else
                        state.pirnDisplZ = Math.min(2.8125F, state.pirnDisplZ + 0.703125F);
                    if (state.pirnDisplZ <= 0 && state.pirnBool)
                        state.pirnBool = false;
                    else if (state.pirnDisplZ >= 2.8125F && !state.pirnBool)
                        state.pirnBool = true;

                    if (state.rackSideBool)
                        state.rackSideDispl = Math.max(0, state.rackSideDispl - 0.0625F);
                    else
                        state.rackSideDispl = Math.min(0.25F, state.rackSideDispl + 0.0625F);
                    if (state.rackSideDispl <= 0 && state.rackSideBool)
                        state.rackSideBool = false;
                    else if (state.rackSideDispl >= 0.25F && !state.rackSideBool)
                        state.rackSideBool = true;
                }

                if (tick <= 15)
                {
                    state.pirnDisplX = 0;
                    state.pirnDisplY = 0;
                    state.pirnDisplZ = 0;
                }
                if (delayedTick % 56 == 18)
                {
                    state.rackSideDispl = 0.25F;
                    state.pirnDisplZ = 0;
                }

                state.pirnDisplX2 = tick <= 20 ? 0 : state.rackDispl * state.rackDispl * (3.0F - 2.0F * state.rackDispl) - 0.72625F;

                state.weaveTextureDispl = delayedTick > 0 ? Math.floorDiv(delayedTick + 14, 28) : 0;

                ++process.processTick;
                context.requestMasterBESync();
            }
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
            if (PIRN_IN_POS.equals(position.posInMultiblock()))
                return state.pirnInputHandler.cast(ctx);
            if (WEAVE_IN_POS.contains(position.posInMultiblock()))
                return state.weaveInputHandler.cast(ctx);
            if (OUT_CAP.contains(position))
                return state.outputHandler.cast(ctx);
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
        if (forType == ShapeType.SELECTION)
            return PowerLoomSelectionShapes.SHAPE_GETTER;
        return PowerLoomShapes.SHAPE_GETTER;
    }

    @Override
    public InteractionResult click(IMultiblockContext<State> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient)
    {
        if (isClient)
            return InteractionResult.SUCCESS;

        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        final State state = ctx.getState();
        final Level level = ctx.getLevel().getRawLevel();
        final ItemStack heldItem = player.getMainHandItem();

        if (PIRN_IN_POS.equals(posInMultiblock))
        {
            IItemHandler insertionHandler = state.pirnInputHandler.getValue();
            if (insertionHandler != null)
            {
                List<ItemStack> list = new ArrayList<>(8);
                for (int i = FIRST_PIRN_IN_SLOT; i < FIRST_PIRN_IN_SLOT + PIRN_IN_SLOT_COUNT; i++)
                    list.add(state.inventory.getStackInSlot(i));
                boolean areAllEmpty = list.stream().allMatch(ItemStack::isEmpty);
                boolean anyMatch = list.stream().anyMatch(stack -> stack.is(heldItem.getItem()));

                if (areAllEmpty || anyMatch)
                {
                    if (PowerLoomRecipe.isValidPirnInput(level, heldItem))
                    {
                        ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, 1);
                        stack = ItemHandlerHelper.insertItem(insertionHandler, stack, false);
                        if (stack.isEmpty())
                        {
                            heldItem.shrink(1);
                            ctx.markDirtyAndSync();
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
                if (player.isShiftKeyDown() && state.processor.getQueueSize() < state.processor.getMaxQueueSize())
                {
                    for (int i = 0; i < 8; i++)
                    {
                        ItemStack stack = state.inventory.getStackInSlot(i);
                        if (!stack.isEmpty())
                        {
                            ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
                            int size = stack.getCount();
                            stack.shrink(size);
                            ctx.markDirtyAndSync();
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        else if (bX == 2 && bY == 0 && (bZ == 1 || bZ == 2 || bZ == 3))
        {
            IItemHandler insertionHandler = state.weaveInputHandler.getValue();
            if (insertionHandler != null)
            {
                for (int i = FIRST_WEAVE_IN_SLOT; i < FIRST_WEAVE_IN_SLOT + WEAVE_IN_SLOT_COUNT; i++)
                {
                    PowerLoomRecipe recipe = PowerLoomRecipe.findRecipeForRendering(level, state.inventory.getStackInSlot(SECONDARY_WEAVE_IN_SLOT));
                    if (recipe != null && recipe.inputs[0].testIgnoringSize(heldItem))
                    {
                        if (state.inventory.getStackInSlot(i).isEmpty())
                        {
                            int size = heldItem.getCount();
                            ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, size);
                            stack = ItemHandlerHelper.insertItem(insertionHandler, stack, false);
                            if (stack.isEmpty())
                            {
                                heldItem.shrink(size);
                                ctx.markDirtyAndSync();
                                return InteractionResult.SUCCESS;
                            }
                        }
                        if (state.inventory.getStackInSlot(i).getCount() < state.inventory.getStackInSlot(i).getMaxStackSize())
                        {
                            ItemStack remainder = ItemHandlerHelper.insertItemStacked(insertionHandler, heldItem, true);
                            if (remainder.isEmpty())
                            {
                                int size = heldItem.getCount() - remainder.getCount();
                                ItemHandlerHelper.insertItemStacked(insertionHandler, heldItem, false);
                                heldItem.shrink(size);
                                ctx.markDirtyAndSync();
                                return InteractionResult.SUCCESS;
                            }
                            else
                            {
                                int size = remainder.getCount();
                                ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, size);
                                stack = ItemHandlerHelper.insertItem(insertionHandler, stack, false);
                                if (stack.isEmpty())
                                {
                                    heldItem.shrink(size);
                                    ctx.markDirtyAndSync();
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                    }
                    if (player.isShiftKeyDown() && state.processor.getQueueSize() < state.processor.getMaxQueueSize())
                    {
                        ItemStack stack = state.inventory.getStackInSlot(i);
                        if (!stack.isEmpty())
                        {
                            ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
                            int size = stack.getCount();
                            stack.shrink(size);
                            ctx.markDirtyAndSync();
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        else if (SECONDARY_WEAVE_IO_POS.equals(posInMultiblock))
        {
            if (player.isShiftKeyDown() && state.processor.getQueueSize() < state.processor.getMaxQueueSize())
            {
                if (state.inventory.getStackInSlot(8).isEmpty() && state.inventory.getStackInSlot(9).isEmpty() && state.inventory.getStackInSlot(10).isEmpty())
                {
                    ItemStack stack = state.inventory.getStackInSlot(SECONDARY_WEAVE_IN_SLOT);
                    if (!stack.isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
                        int size = stack.getCount();
                        stack.shrink(size);
                        ctx.markDirtyAndSync();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            else
            {
                PowerLoomRecipe recipe = PowerLoomRecipe.findRecipeForRendering(ctx.getLevel().getRawLevel(), heldItem);
                if (recipe != null)
                {
                    ItemStack secondarySlot = state.inventory.getStackInSlot(SECONDARY_WEAVE_IN_SLOT);
                    if (secondarySlot.isEmpty())
                    {
                        int size = Math.min(heldItem.getCount(), recipe.secondaryInput.getCount());
                        ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, size);
                        state.inventory.setStackInSlot(SECONDARY_WEAVE_IN_SLOT, stack);
                        heldItem.shrink(size);
                        ctx.markDirtyAndSync();
                        return InteractionResult.SUCCESS;
                    }
                    if (secondarySlot.is(heldItem.getItem()) && secondarySlot.getCount() < recipe.secondaryInput.getCount())
                    {
                        int remaining = recipe.secondaryInput.getCount() - secondarySlot.getCount();
                        int size = Math.min(heldItem.getCount(), remaining);
                        ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, size);
                        state.inventory.insertItem(SECONDARY_WEAVE_IN_SLOT, stack, false);
                        heldItem.shrink(size);
                        ctx.markDirtyAndSync();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        else if (bX == 0 && bY == 0 && (bZ == 1 || bZ == 2 || bZ == 3))
        {
            if (player.isShiftKeyDown())
            {
                ItemStack stack = state.inventory.getStackInSlot(OUT_SLOT);
                if (!stack.isEmpty())
                {
                    ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
                    int size = stack.getCount();
                    stack.shrink(size);
                    ctx.markDirtyAndSync();
                    return InteractionResult.SUCCESS;
                }
            }
        }
        else if (ATTConfig.SERVER.enablePowerLoomDebug.get())
            if (player.getMainHandItem().is(ATTItems.PIRN.get()))
                player.openMenu(ATTContainerTypes.POWER_LOOM.provide(ctx, posInMultiblock));
        return InteractionResult.SUCCESS;
    }

    public static class State implements IMultiblockState, ATTProcessContext<PowerLoomRecipe>
    {
        private final AveragingEnergyStorage energy = new AveragingEnergyStorage(ENERGY_CAPACITY);
        private final MultiblockProcessor<PowerLoomRecipe, ATTProcessContext<PowerLoomRecipe>> processor;
        public final PowerLoomInventory inventory;
        public final RedstoneControl.RSState rsState = RedstoneControl.RSState.enabledByDefault();

        private final CapabilityReference<IItemHandler> output;
        private final DroppingMultiblockOutput secondaryOutput;
        private final StoredCapability<IEnergyStorage> energyCap;
        private final StoredCapability<IItemHandler> pirnInputHandler;
        private final StoredCapability<IItemHandler> weaveInputHandler;
        //        private final StoredCapability<IItemHandler> secondaryWeaveInputHandler;
        private final StoredCapability<IItemHandler> outputHandler;

        // Client
        private boolean active;
        public float rodAngle = 0;
        public float rackDispl = 0;
        public float rackSideDispl = 0;
        public float rack2Displ = 0;
        public float longThreadAngle = 0;
        public float shortThreadAngle = 0;
        public float pirnAngle = 0;
        public float pirnDisplX = 0;
        public float pirnDisplX2 = 0;
        public float pirnDisplY = 0;
        public float pirnDisplZ = 0;
        public int weaveTextureDispl = 0;
        public int holderRotation;
        public boolean rackBool = true;
        public boolean rackSideBool = true;
        public boolean rack2Bool = true;
        public boolean pirnBool = true;
        public ResourceLocation lastTexture = new ResourceLocation("forge:white");

        public State(IInitialMultiblockContext<State> ctx)
        {
            final Runnable markDirty = ctx.getMarkDirtyRunnable();
            final Supplier<@Nullable Level> getLevel = ctx.levelSupplier();
            List<SlotwiseItemHandler.IOConstraintGroup> constraintGroups = List.of(
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.input(stack -> PowerLoomRecipe.isValidPirnInput(getLevel.get(), stack)), PIRN_IN_SLOT_COUNT),
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.input(stack -> PowerLoomRecipe.isValidWeaveInput(getLevel.get(), stack)), WEAVE_IN_SLOT_COUNT),
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.ANY_INPUT, 1),
                new SlotwiseItemHandler.IOConstraintGroup(SlotwiseItemHandler.IOConstraint.OUTPUT, 1)
            );
            List<SlotwiseItemHandler.IOConstraint> slotConstraints = new ArrayList<>();
            for (final SlotwiseItemHandler.IOConstraintGroup group : constraintGroups)
                for (int i = 0; i < group.slotCount(); i++)
                    slotConstraints.add(group.constraint());
            this.inventory = new PowerLoomInventory(slotConstraints, markDirty);
            this.processor = new MultiblockProcessor<>(
                1, 0, 1, markDirty, PowerLoomRecipe.RECIPES::getById
            );
            this.output = ctx.getCapabilityAt(ForgeCapabilities.ITEM_HANDLER, MAIN_OUT_POS);
            this.secondaryOutput = new DroppingMultiblockOutput(SECONDARY_OUT_POS, ctx);
            this.energyCap = new StoredCapability<>(this.energy);
            this.pirnInputHandler = new StoredCapability<>(new WrappingItemHandler(
                inventory, true, false, new WrappingItemHandler.IntRange(FIRST_PIRN_IN_SLOT, FIRST_PIRN_IN_SLOT + PIRN_IN_SLOT_COUNT)
            ));
            this.weaveInputHandler = new StoredCapability<>(new WrappingItemHandler(
                inventory, true, false, new WrappingItemHandler.IntRange(FIRST_WEAVE_IN_SLOT, FIRST_WEAVE_IN_SLOT + WEAVE_IN_SLOT_COUNT)
            ));
            this.outputHandler = new StoredCapability<>(new WrappingItemHandler(
                inventory, false, true, new WrappingItemHandler.IntRange(OUT_SLOT, 1)
            ));
        }

        @Override
        public void writeSaveNBT(CompoundTag nbt)
        {
            writeCommonNBT(nbt);
            nbt.put("energy", energy.serializeNBT());
        }

        @Override
        public void readSaveNBT(CompoundTag nbt)
        {
            readCommonNBT(nbt);
            energy.deserializeNBT(nbt.get("energy"));
        }

        @Override
        public void writeSyncNBT(CompoundTag nbt)
        {
            writeCommonNBT(nbt);
            nbt.putBoolean("active", active);
            nbt.putInt("holderRotation", holderRotation);
        }

        @Override
        public void readSyncNBT(CompoundTag nbt)
        {
            readCommonNBT(nbt);
            active = nbt.getBoolean("active");
            holderRotation = nbt.getInt("holderRotation");
        }

        private void writeCommonNBT(CompoundTag nbt)
        {
            nbt.put("processor", processor.toNBT());
            nbt.put("inventory", inventory.serializeNBT());
            nbt.putString("lastTexture", lastTexture.toString());
        }

        private void readCommonNBT(CompoundTag nbt)
        {
            processor.fromNBT(nbt.get("processor"), ATTMultiblockProcess::new);
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            lastTexture = nbt.contains("lastTexture", Tag.TAG_STRING) ? new ResourceLocation(nbt.getString("lastTexture")) : new ResourceLocation("forge:white");
        }

        @Override
        public AveragingEnergyStorage getEnergy()
        {
            return energy;
        }

        @Override
        public IItemHandlerModifiable getInventory()
        {
            return inventory;
        }

        @Override
        public int[] getOutputSlots()
        {
            return new int[] {OUT_SLOT};
        }

        @Override
        public void doProcessOutput(ItemStack output, IMultiblockLevel level)
        {
            this.secondaryOutput.insertOrDrop(output, level);
        }

        @Override
        public void onProcessFinish(MultiblockProcess<PowerLoomRecipe, ?> process, Level level)
        {
            pirnAngle = 0;
            holderRotation = (holderRotation + 1) % 8;
        }

        public boolean shouldRenderAsActive()
        {
            return active;
        }

        public List<MultiblockProcess<PowerLoomRecipe, ATTProcessContext<PowerLoomRecipe>>> getProcessQueue()
        {
            return processor.getQueue();
        }
    }
}
