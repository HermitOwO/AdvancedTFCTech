package com.hermitowo.advancedtfctech.common.blockentities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.orientation.RelativeBlockFace;
import com.google.common.collect.ImmutableSet;
import com.hermitowo.advancedtfctech.api.crafting.PowerLoomRecipe;
import com.hermitowo.advancedtfctech.common.blocks.ticking.ATTCommonTickableBlock;
import com.hermitowo.advancedtfctech.common.container.ATTContainerProvider;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.multiblocks.PowerLoomMultiblock;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class PowerLoomBlockEntity extends PoweredMultiblockBlockEntity<PowerLoomBlockEntity, PowerLoomRecipe> implements ATTCommonTickableBlock, ATTContainerProvider<PowerLoomBlockEntity>, IEBlockInterfaces.IBlockBounds, IEBlockInterfaces.IPlayerInteraction
{
    private static final BlockPos OUT_POS_1 = new BlockPos(0, 0, 1);
    private static final BlockPos OUT_POS_2 = new BlockPos(0, 0, 2);
    private static final BlockPos OUT_POS_3 = new BlockPos(0, 0, 3);
    private static final BlockPos SECONDARY_OUT_POS = new BlockPos(2, 0, 4);

    private static final BlockPos PIRN_IN_POS = new BlockPos(1, 1, 4);
    private static final BlockPos WEAVE_IN_POS_1 = new BlockPos(2, 0, 1);
    private static final BlockPos WEAVE_IN_POS_2 = new BlockPos(2, 0, 2);
    private static final BlockPos WEAVE_IN_POS_3 = new BlockPos(2, 0, 3);
    private static final BlockPos SECONDARY_WEAVE_IN_POS = new BlockPos(1, 1, 2);

    public NonNullList<ItemStack> inventory = NonNullList.withSize(13, ItemStack.EMPTY);
    public List<ItemStack> pirnList = inventory.subList(0, 8);
    public List<ItemStack> inputList = inventory.subList(8, 11);

    private final CapabilityReference<IItemHandler> secondaryInput = CapabilityReference.forBlockEntityAt(this,
        () -> new DirectionalBlockPos(this.getBlockPosForPos(PIRN_IN_POS).relative(getFacing(), 2), getFacing().getOpposite()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    private final CapabilityReference<IItemHandler> weaveInput = CapabilityReference.forBlockEntityAt(this,
        () -> new DirectionalBlockPos(this.getBlockPosForPos(WEAVE_IN_POS_2).relative(getFacing()), getFacing().getOpposite()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    private final CapabilityReference<IItemHandler> pirnInput = CapabilityReference.forBlockEntityAt(this,
        () -> new DirectionalBlockPos(this.getBlockPosForPos(SECONDARY_WEAVE_IN_POS).relative(getFacing(), -2), getFacing().getOpposite()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    private final CapabilityReference<IItemHandler> output = CapabilityReference.forBlockEntityAt(this,
        () -> {
            Direction outDir = getIsMirrored() ? getFacing().getClockWise() : getFacing().getCounterClockWise();
            return new DirectionalBlockPos(this.getBlockPosForPos(OUT_POS_2).relative(outDir, 1), outDir.getOpposite());
        }, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    private final CapabilityReference<IItemHandler> secondaryOutput = CapabilityReference.forBlockEntityAt(this,
        () -> {
            Direction outDir = getIsMirrored() ? getFacing().getCounterClockWise() : getFacing().getClockWise();
            return new DirectionalBlockPos(this.getBlockPosForPos(SECONDARY_OUT_POS).relative(outDir, 1), outDir);
        }, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    public PowerLoomBlockEntity(BlockEntityType<PowerLoomBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(PowerLoomMultiblock.INSTANCE, 32000, true, type, pos, state);
    }

    public float animation_rodRotation = 0;
    public float animation_rack = 0;
    public float animation_rack_side = 0;
    public float animation_rack2 = 0;
    public float angle_long_thread = 0;
    public float angle_short_thread = 0;
    public float animation_pirn = 0;
    public float animation_pirn_x = 0;
    public float animation_pirn_x2 = 0;
    public float animation_pirn_y = 0;
    public float animation_pirn_z = 0;
    public int animation_weave = 0;
    public int holderRotation;
    public boolean animation_rack_b = true;
    public boolean animation_rack_side_b = true;
    public boolean animation_rack2_b = true;
    public boolean animation_pirn_b = true;
    public ResourceLocation lastTexture;

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        Collections.fill(inventory, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, inventory);
        this.holderRotation = nbt.getInt("holderRotation");
        this.lastTexture = nbt.contains("lastTexture", Tag.TAG_STRING) ? new ResourceLocation(nbt.getString("lastTexture")) : new ResourceLocation("forge:white");
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        ContainerHelper.saveAllItems(nbt, inventory);
        nbt.putInt("holderRotation", this.holderRotation);
        if (!this.lastTexture.toString().equals("forge:white"))
            nbt.putString("lastTexture", this.lastTexture.toString());
    }

    @Override
    public void tickClient()
    {
        if (shouldRenderAsActive())
        {
            if (processQueue.isEmpty())
            {
                if (animation_rack < 0.65625F)
                    animation_rack = Math.min(0.65625F, animation_rack + 0.046875F);

                if (animation_rack_side < 0.25F)
                    animation_rack_side = Math.min(0.25F, animation_rack_side + 0.125F);

                if (animation_rack2 < 0.4375F)
                    animation_rack2 = Math.min(0.4375F, animation_rack2 + 0.0875F);

                if (angle_long_thread < 19.0F)
                    angle_long_thread = Math.min(19.0F, angle_long_thread + 3.8F);

                if (angle_short_thread < 42.0F)
                    angle_short_thread = Math.min(42.0F, angle_short_thread + 8.4F);

                animation_pirn = 0;
                animation_pirn_x = 0;
                animation_pirn_y = 0;
                animation_pirn_z = 0;
            }

            animation_rodRotation += 1.75f;
            animation_rodRotation %= 360f;

            for (MultiblockProcess<PowerLoomRecipe> process : this.processQueue)
            {
                int tick = process.processTick;
                int delayedTick = tick - 20;

                if (delayedTick > 0)
                {
                    if (animation_rack_b)
                        animation_rack = Math.max(0, animation_rack - 0.046875F);
                    else
                        animation_rack = Math.min(0.65625F, animation_rack + 0.046875F);
                    if (animation_rack <= 0 && animation_rack_b)
                        animation_rack_b = false;
                    else if (animation_rack >= 0.65625F && !animation_rack_b)
                        animation_rack_b = true;
                }
                else if (animation_rack < 0.65625F)
                    animation_rack = Math.min(0.65625F, animation_rack + 0.046875F);

                if (delayedTick / 28 >= 1 && delayedTick % 28 <= 4)
                {
                    if (animation_rack2_b)
                    {
                        animation_rack2 = Math.max(0, animation_rack2 - 0.0875F);
                        angle_long_thread = Math.max(0, angle_long_thread - 3.8F);
                        angle_short_thread = Math.max(0, angle_short_thread - 8.4F);
                    }
                    else
                    {
                        animation_rack2 = Math.min(0.4375F, animation_rack2 + 0.0875F);
                        angle_long_thread = Math.min(19.0F, angle_long_thread + 3.8F);
                        angle_short_thread = Math.min(42.0F, angle_short_thread + 8.4F);
                    }
                    if (animation_rack2 <= 0 && animation_rack2_b)
                        animation_rack2_b = false;
                    else if (animation_rack2 >= 0.4375F && !animation_rack2_b)
                        animation_rack2_b = true;
                }

                if (tick <= 15)
                    animation_pirn = 3.0F * tick;
                else
                    animation_pirn = 45.0F;

                if (tick > 15 && tick <= 20)
                {
                    animation_pirn_x = 0.02965F * (tick - 15);
                    animation_pirn_y = 0.046875F * (tick - 15);
                }
                else
                {
                    animation_pirn_x = 0.14825F;
                    animation_pirn_y = 0.234375F;
                }

                if (delayedTick % 56 > 10 && delayedTick % 56 <= 18)
                {
                    if (animation_pirn_b)
                        animation_pirn_z = Math.max(0, animation_pirn_z - 0.703125F);
                    else
                        animation_pirn_z = Math.min(2.8125F, animation_pirn_z + 0.703125F);
                    if (animation_pirn_z <= 0 && animation_pirn_b)
                        animation_pirn_b = false;
                    else if (animation_pirn_z >= 2.8125F && !animation_pirn_b)
                        animation_pirn_b = true;

                    if (animation_rack_side_b)
                        animation_rack_side = Math.max(0, animation_rack_side - 0.0625F);
                    else
                        animation_rack_side = Math.min(0.25F, animation_rack_side + 0.0625F);
                    if (animation_rack_side <= 0 && animation_rack_side_b)
                        animation_rack_side_b = false;
                    else if (animation_rack_side >= 0.25F && !animation_rack_side_b)
                        animation_rack_side_b = true;
                }

                if (tick <= 15)
                {
                    animation_pirn_x = 0;
                    animation_pirn_y = 0;
                    animation_pirn_z = 0;
                }
                if (delayedTick % 56 == 18)
                {
                    animation_rack_side = 0.25F;
                    animation_pirn_z = 0;
                }

                animation_pirn_x2 = tick <= 20 ? 0 : animation_rack * animation_rack * (3.0F - 2.0F * animation_rack) - 0.72625F;

                animation_weave = delayedTick > 0 ? Math.floorDiv(delayedTick + 14, 28) : 0;
            }
        }
    }

    @Override
    public void tickServer()
    {
        if (isDummy())
            return;

        super.tickServer();
        boolean update = false;

        if (!isRSDisabled() && energyStorage.getEnergyStored() > 0)
        {
            if (this.processQueue.size() < this.getProcessQueueMaxLength())
            {
                for (int i = 0; i < 8; i++)
                {
                    ItemStack pirn = this.inventory.get(i);
                    for (int j = 8; j < 11; j++)
                    {
                        ItemStack weave = this.inventory.get(j);
                        if (!pirn.isEmpty() && !weave.isEmpty())
                        {
                            PowerLoomRecipe recipe = PowerLoomRecipe.findRecipe(level, pirn, weave);
                            ItemStack secondarySlot = this.inventory.get(11);
                            if (recipe != null && recipe.secondaryInput.test(secondarySlot))
                            {
                                ItemStack outputSlot = this.inventory.get(12);
                                ItemStack output = recipe.output.get();
                                if (outputSlot.isEmpty() || (ItemHandlerHelper.canItemStacksStack(outputSlot, output) && outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize()))
                                {
                                    MultiblockProcessInMachine<PowerLoomRecipe> process = new MultiblockProcessInMachine<>(recipe, this::getRecipeForId, i, j);
                                    if (this.addProcessToQueue(process, true))
                                    {
                                        this.addProcessToQueue(process, false);
                                        update = true;

                                        this.lastTexture = recipe.inProgressTexture;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            assert level != null;
            if (level.getGameTime() % 8 == 0)
            {
                sort();
                IItemHandler outputHandler = output.getNullable();
                if (outputHandler != null)
                {
                    if (!inventory.get(12).isEmpty())
                    {
                        ItemStack stack = ItemHandlerHelper.copyStackWithSize(inventory.get(12), 1);
                        stack = ItemHandlerHelper.insertItem(outputHandler, stack, false);
                        if (stack.isEmpty())
                        {
                            this.inventory.get(12).shrink(1);
                            if (this.inventory.get(12).getCount() <= 0)
                            {
                                this.inventory.set(12, ItemStack.EMPTY);
                                update = true;
                            }
                        }
                    }
                }
            }
        }
        if (update)
        {
            this.setChanged();
            this.markContainingBlockForUpdate(null);
        }
    }

    public void sort()
    {
        for (int i = 0; i < inputList.size(); i++)
        {
            for (int j = i + 1; j < inputList.size(); j++)
            {
                ItemStack holder1 = inputList.get(i);
                ItemStack holder2 = inputList.get(j);
                int size1 = holder1.getCount();
                int size2 = holder2.getCount();
                int sizeMax = holder1.getMaxStackSize();
                if (!holder1.isEmpty() && size1 < sizeMax && !holder2.isEmpty() && size2 < sizeMax)
                {
                    if (size1 + size2 > sizeMax)
                    {
                        if (size1 >= size2)
                        {
                            int amount = sizeMax - size2;
                            this.inventory.get(i + 8).shrink(amount);
                            this.inventory.get(j + 8).grow(amount);
                        }
                        else
                        {
                            int amount = sizeMax - size1;
                            this.inventory.get(i + 8).grow(amount);
                            this.inventory.get(j + 8).shrink(amount);
                        }
                    }
                    else
                    {
                        ItemStack stack = new ItemStack(inputList.get(j).getItem(), inputList.get(i).getCount() + inputList.get(j).getCount());
                        this.inventory.set(i + 8, stack);
                        this.inventory.set(j + 8, ItemStack.EMPTY);
                    }
                    sort();
                }
            }
        }
    }

    @Override
    public boolean interact(Direction side, Player player, InteractionHand hand, ItemStack heldItem, float hitX, float hitY, float hitZ)
    {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        PowerLoomBlockEntity master = master();
        if (master != null)
        {
            if (bX == 1 && bY == 1 && bZ == 4)
            {
                IItemHandler insertionHandler = pirnInput.getNullable();
                if (insertionHandler != null)
                {
                    List<ItemStack> list = new ArrayList<>(8);
                    for (int i = 0; i < 8; i++)
                        list.add(master.inventory.get(i));
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
                                return true;
                            }
                        }
                    }
                    if (player.isShiftKeyDown() && master.processQueue.size() < master.getProcessQueueMaxLength())
                    {
                        for (int i = 0; i < 8; i++)
                        {
                            ItemStack stack = master.inventory.get(i);
                            if (!stack.isEmpty())
                            {
                                ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
                                int size = stack.getCount();
                                stack.shrink(size);
                                return true;
                            }
                        }
                    }
                }
            }
            if (bX == 2 && bY == 0 && (bZ == 1 || bZ == 2 || bZ == 3))
            {
                IItemHandler insertionHandler = weaveInput.getNullable();
                if (insertionHandler != null)
                {
                    for (int i = 8; i < 11; i++)
                    {
                        PowerLoomRecipe recipe = PowerLoomRecipe.findRecipeForRendering(level, master.inventory.get(11));
                        if (recipe != null && recipe.inputs[0].testIgnoringSize(heldItem))
                        {
                            if (master.inventory.get(i).isEmpty())
                            {
                                int size = heldItem.getCount();
                                ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, size);
                                stack = ItemHandlerHelper.insertItem(insertionHandler, stack, false);
                                if (stack.isEmpty())
                                {
                                    heldItem.shrink(size);
                                    return true;
                                }
                            }
                            if (master.inventory.get(i).getCount() < master.inventory.get(i).getMaxStackSize())
                            {
                                int remaining = master.inventory.get(i).getMaxStackSize() - master.inventory.get(i).getCount();
                                int size = Math.min(heldItem.getCount(), remaining);
                                ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, size);
                                stack = ItemHandlerHelper.insertItem(insertionHandler, stack, false);
                                if (stack.isEmpty())
                                {
                                    heldItem.shrink(size);
                                    return true;
                                }
                            }
                        }
                        if (player.isShiftKeyDown() && master.processQueue.size() < master.getProcessQueueMaxLength())
                        {
                            ItemStack stack = master.inventory.get(i);
                            if (!stack.isEmpty())
                            {
                                ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
                                int size = stack.getCount();
                                stack.shrink(size);
                                return true;
                            }
                        }
                    }
                }
            }
            if (bX == 1 && bY == 1 && bZ == 2)
            {
                if (player.isShiftKeyDown() && master.processQueue.size() < master.getProcessQueueMaxLength())
                {
                    if (master.inventory.get(8).isEmpty() && master.inventory.get(9).isEmpty() && master.inventory.get(10).isEmpty())
                    {
                        ItemStack stack = master.inventory.get(11);
                        if (!stack.isEmpty())
                        {
                            ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
                            int size = stack.getCount();
                            stack.shrink(size);
                            return true;
                        }
                    }
                }
                IItemHandler insertionHandler = secondaryInput.getNullable();
                if (insertionHandler != null)
                {
                    PowerLoomRecipe recipe = PowerLoomRecipe.findRecipeForRendering(level, heldItem);
                    if (recipe != null)
                    {
                        ItemStack secondarySlot = master.inventory.get(11);
                        if (secondarySlot.isEmpty())
                        {
                            int size = Math.min(heldItem.getCount(), recipe.secondaryInput.getCount());
                            ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, size);
                            stack = ItemHandlerHelper.insertItem(insertionHandler, stack, false);
                            if (stack.isEmpty())
                            {
                                heldItem.shrink(size);
                                return true;
                            }
                        }
                        if (secondarySlot.is(heldItem.getItem()) && secondarySlot.getCount() < recipe.secondaryInput.getCount())
                        {
                            int remaining = recipe.secondaryInput.getCount() - secondarySlot.getCount();
                            int size = Math.min(heldItem.getCount(), remaining);
                            ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, size);
                            stack = ItemHandlerHelper.insertItem(insertionHandler, stack, false);
                            if (stack.isEmpty())
                            {
                                heldItem.shrink(size);
                                return true;
                            }
                        }
                    }
                }
            }
            if (bX == 0 && bY == 0 && (bZ == 1 || bZ == 2 || bZ == 3))
            {
                if (player.isShiftKeyDown())
                {
                    ItemStack stack = master.inventory.get(12);
                    if (!stack.isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
                        int size = stack.getCount();
                        stack.shrink(size);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Set<MultiblockFace> getEnergyPos()
    {
        return ImmutableSet.of(new MultiblockFace(1, 1, 0, RelativeBlockFace.UP));
    }

    @Override
    public Set<BlockPos> getRedstonePos()
    {
        return ImmutableSet.of(new BlockPos(1, 0, 4));
    }

    private final MultiblockCapability<IItemHandler> pirnInputHandler = MultiblockCapability.make(
        this, be -> be.pirnInputHandler, PowerLoomBlockEntity::master,
        registerCapability(new IEInventoryHandler(8, this, 0, true, false))
    );

    private final MultiblockCapability<IItemHandler> weaveInputHandler = MultiblockCapability.make(
        this, be -> be.weaveInputHandler, PowerLoomBlockEntity::master,
        registerCapability(new IEInventoryHandler(3, this, 8, true, false))
    );

    private final MultiblockCapability<IItemHandler> secondaryInputHandler = MultiblockCapability.make(
        this, be -> be.secondaryInputHandler, PowerLoomBlockEntity::master,
        registerCapability(new IEInventoryHandler(1, this, 11, true, false))
    );

    private final MultiblockCapability<IItemHandler> outputHandler = MultiblockCapability.make(
        this, be -> be.outputHandler, PowerLoomBlockEntity::master,
        registerCapability(new IEInventoryHandler(1, this, 12, false, true))
    );

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (posInMultiblock.equals(PIRN_IN_POS))
                return pirnInputHandler.getAndCast();
            if (posInMultiblock.equals(WEAVE_IN_POS_1) || posInMultiblock.equals(WEAVE_IN_POS_2) || posInMultiblock.equals(WEAVE_IN_POS_3))
                return weaveInputHandler.getAndCast();
            if (posInMultiblock.equals(SECONDARY_WEAVE_IN_POS))
                return secondaryInputHandler.getAndCast();
            if (posInMultiblock.equals(OUT_POS_1) || posInMultiblock.equals(OUT_POS_2) || posInMultiblock.equals(OUT_POS_3))
                return outputHandler.getAndCast();
        }
        return super.getCapability(capability, facing);
    }

    @Nullable
    @Override
    protected PowerLoomRecipe getRecipeForId(Level level, ResourceLocation id)
    {
        return PowerLoomRecipe.RECIPES.getById(level, id);
    }

    @Override
    public PowerLoomRecipe findRecipeForInsertion(ItemStack inserting)
    {
        return null;
    }

    @Nullable
    @Override
    public IFluidTank[] getInternalTanks()
    {
        return null;
    }

    @Override
    public int[] getOutputTanks()
    {
        return null;
    }

    @Override
    public int[] getOutputSlots()
    {
        return new int[] {12};
    }

    @Override
    public PowerLoomBlockEntity getGuiMaster()
    {
        return master();
    }

    @Override
    public boolean canUseGui(Player player)
    {
        if (ATTConfig.SERVER.enablePowerLoomDebug.get())
            return formed && player.getMainHandItem().is(ATTItems.PIRN.get());
        return false;
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory()
    {
        return this.inventory;
    }

    @NotNull
    @Override
    public BEContainerATT<? super PowerLoomBlockEntity, ?> getContainerTypeATT()
    {
        return ATTContainerTypes.POWER_LOOM;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack)
    {
        if (slot >= 0 && slot < 8)
            return PowerLoomRecipe.isValidPirnInput(level, stack);
        if (slot >= 8 && slot < 11)
            return PowerLoomRecipe.isValidWeaveInput(level, stack);
        if (slot == 11)
            return PowerLoomRecipe.isValidSecondaryInput(level, stack);
        return false;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        if (slot >= 0 && slot < 8)
            return 1;
        return 64;
    }

    @Override
    public void doGraphicalUpdates()
    {
        this.setChanged();
        this.markContainingBlockForUpdate(null);
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<PowerLoomRecipe> process)
    {
        PowerLoomRecipe recipe = process.getRecipe(level);
        ItemStack stack = inventory.get(11);
        if (recipe != null && !stack.isEmpty())
            return recipe.secondaryInput.test(stack);
        return false;
    }

    @Override
    public void doProcessOutput(ItemStack output)
    {
    }

    @Override
    public void doProcessFluidOutput(FluidStack output)
    {
    }

    @Override
    public void onProcessFinish(MultiblockProcess<PowerLoomRecipe> process)
    {
        PowerLoomRecipe recipe = process.getRecipe(level);
        assert recipe != null;
        for (Lazy<ItemStack> out : recipe.secondaryOutputs)
        {
            ItemStack stack = out.get();
            stack = Utils.insertStackIntoInventory(this.secondaryOutput, stack, false);
            if (!stack.isEmpty())
            {
                Direction outDir = getIsMirrored() ? getFacing().getCounterClockWise() : getFacing().getClockWise();
                BlockPos pos = this.getBlockPosForPos(SECONDARY_OUT_POS).relative(outDir, 1);
                Utils.dropStackAtPos(level, pos, stack, outDir);
            }
        }

        animation_pirn = 0;
        this.holderRotation = (holderRotation + 1) % 8;
    }

    @Override
    public int getMaxProcessPerTick()
    {
        return 1;
    }

    @Override
    public int getProcessQueueMaxLength()
    {
        return 1;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<PowerLoomRecipe> process)
    {
        return 0;
    }

    @Override
    public boolean isInWorldProcessingMachine()
    {
        return false;
    }

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(PowerLoomBlockEntity::getShape);

    @NotNull
    @Override
    public VoxelShape getBlockBounds(@Nullable CollisionContext ctx)
    {
        return SHAPES.get(this.posInMultiblock, Pair.of(getFacing(), getIsMirrored()));
    }

    private static List<AABB> getShape(BlockPos posInMultiblock)
    {
        final int bX = posInMultiblock.getX();
        final int bY = posInMultiblock.getY();
        final int bZ = posInMultiblock.getZ();

        List<AABB> main = new ArrayList<>();

        // Base
        if (bY == 0)
        {
            if (bX == 0)
            {
                main.add(box(0, 0, 0, 16, 4, 16));
                main.add(box(9, 4, 0, 16, 16, 16));

                if (bZ == 1)
                {
                    main.add(box(0, 4, 1, 8, 16, 5));
                    main.add(box(8, 4, 0, 9, 16, 6));
                }
                if (bZ == 3)
                {
                    main.add(box(0, 4, 11, 8, 16, 15));
                    main.add(box(8, 4, 10, 9, 16, 16));
                }
            }
            if (bX == 2 && bZ != 4)
            {
                main.add(box(0, 0, 0, 16, 4, 16));
                main.add(box(0, 4, 0, 5, 16, 16));

                if (bZ == 1)
                {
                    main.add(box(5.5, 4, 1, 15.5, 16, 5));
                    main.add(box(5, 4, 1, 6, 16, 6));
                }
                if (bZ == 3)
                {
                    main.add(box(5.5, 4, 11, 15.5, 16, 15));
                    main.add(box(5, 4, 10, 6, 16, 16));
                }
            }
        }

        // Second Layer
        if (bY == 1)
        {
            if (bX == 0)
            {
                if (bZ == 1)
                {
                    main.add(box(0, 0, 1, 10, 12, 5));
                    main.add(box(8, 0, 0, 16, 4, 6));
                }
                if (bZ == 3)
                {
                    main.add(box(0, 0, 11, 10, 12, 15));
                    main.add(box(8, 0, 10, 16, 4, 16));
                }
            }
            if (bX == 1)
            {
                if (bZ == 1)
                {
                    main.add(box(8, 0, 0, 16, 16, 6));
                }
                if (bZ == 2)
                {
                    main.add(box(3, 4, 0, 8, 16, 16));
                }
                if (bZ == 3)
                {
                    main.add(box(8, 0, 10, 16, 16, 16));
                }
                if (bZ == 4)
                {
                    main.add(box(9.5, 7, 0, 15.5, 13, 10));
                }
            }
            if (bX == 2)
            {
                if (bZ == 1)
                {
                    main.add(box(6, 0, 1, 15.5, 12, 5));
                    main.add(box(0, 0, 0, 6, 16, 6));
                }
                if (bZ == 3)
                {
                    main.add(box(6, 0, 11, 15.5, 12, 15));
                    main.add(box(0, 0, 10, 6, 16, 16));
                }
            }
        }

        // Top
        if (bY == 2)
        {
            if (bX == 1)
            {
                main.add(box(8, 9, 0, 16, 15, 16));

                if (bZ == 1)
                    main.add(box(8, 0, 0, 16, 9.5, 6));
                if (bZ == 3)
                    main.add(box(8, 0, 10, 16, 9.5, 16));
            }
            if (bX == 2)
            {
                main.add(box(0, 9, 0, 6, 15, 16));

                if (bZ == 1)
                    main.add(box(0, 0, 0, 6, 9.5, 6));
                if (bZ == 3)
                    main.add(box(0, 0, 10, 6, 9.5, 16));
            }
        }

        if (main.isEmpty())
            main.add(new AABB(0, 0, 0, 1, 1, 1));
        return main;
    }

    private static AABB box(double x0, double y0, double z0, double x1, double y1, double z1)
    {
        return new AABB(x0 / 16D, y0 / 16D, z0 / 16D, x1 / 16D, y1 / 16D, z1 / 16D);
    }
}
