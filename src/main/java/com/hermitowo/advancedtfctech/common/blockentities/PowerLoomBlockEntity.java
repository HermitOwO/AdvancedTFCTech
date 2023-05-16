package com.hermitowo.advancedtfctech.common.blockentities;

import java.util.ArrayList;
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
import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public NonNullList<ItemStack> inventory = NonNullList.withSize(14, ItemStack.EMPTY);

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
        () -> new DirectionalBlockPos(this.getBlockPosForPos(OUT_POS_2).relative(getFacing().getCounterClockWise(), 1), getFacing().getClockWise()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    private final CapabilityReference<IItemHandler> secondaryOutput = CapabilityReference.forBlockEntityAt(this,
        () -> new DirectionalBlockPos(this.getBlockPosForPos(SECONDARY_OUT_POS).relative(getFacing().getClockWise(), 1), getFacing().getClockWise()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    public PowerLoomBlockEntity(BlockEntityType<PowerLoomBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(PowerLoomMultiblock.INSTANCE, 32000, true, type, pos, state);
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        if (!descPacket)
            ContainerHelper.loadAllItems(nbt, inventory);
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        if (!descPacket)
            ContainerHelper.saveAllItems(nbt, inventory);
    }

    @Override
    public void tickClient()
    {

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
                    ItemStack pirn = this.getInventory().get(i);
                    for (int j = 8; j < 11; j++)
                    {
                        ItemStack weave = this.getInventory().get(j);
                        if (!pirn.isEmpty() && !weave.isEmpty())
                        {
                            PowerLoomRecipe recipe = PowerLoomRecipe.findRecipe(level, pirn, weave);
                            if (recipe != null && (this.getInventory().get(12).isEmpty() || this.getInventory().get(13).isEmpty()))
                            {
                                MultiblockProcessInMachine<PowerLoomRecipe> process = new MultiblockProcessInMachine<>(recipe, this::getRecipeForId, i, j);
                                if (this.addProcessToQueue(process, true))
                                {
                                    this.addProcessToQueue(process, false);
                                    update = true;
                                }
                            }
                        }
                    }
                }
            }
            if (level.getGameTime() % 8 == 0)
            {
                IItemHandler outputHandler = output.getNullable();
                if (outputHandler != null)
                {
                    for (int j = 12; j < 14; j++)
                    {
                        if (!inventory.get(j).isEmpty())
                        {
                            ItemStack stack = ItemHandlerHelper.copyStackWithSize(inventory.get(j), 1);
                            stack = ItemHandlerHelper.insertItem(outputHandler, stack, false);
                            if (stack.isEmpty())
                            {
                                this.inventory.get(j).shrink(1);
                                if (this.inventory.get(j).getCount() <= 0)
                                    this.inventory.set(j, ItemStack.EMPTY);
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

    @Override
    public void doGraphicalUpdates()
    {
        this.setChanged();
        this.markContainingBlockForUpdate(null);
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
                    for (int i = 0; i < 8; i++)
                    {
                        if (master.inventory.get(i).isEmpty() && PowerLoomRecipe.isValidPirnInput(level, heldItem))
                        {
                            ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, 1);
                            stack = ItemHandlerHelper.insertItem(insertionHandler, stack, false);
                            if (stack.isEmpty())
                            {
                                heldItem.shrink(1);
                                return true;
                            }
                        }
                        if (player.isShiftKeyDown())
                        {
                            ItemStack stack = master.inventory.get(i);
                            if (!stack.isEmpty())
                            {
                                player.getInventory().add(stack);
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
                        if (master.inventory.get(i).isEmpty() && PowerLoomRecipe.isValidWeaveInput(level, heldItem))
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
                        if (player.isShiftKeyDown())
                        {
                            ItemStack stack = master.inventory.get(i);
                            if (!stack.isEmpty())
                            {
                                player.getInventory().add(stack);
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
                IItemHandler insertionHandler = secondaryInput.getNullable();
                if (insertionHandler != null)
                {
                    if (master.inventory.get(11).isEmpty() && PowerLoomRecipe.isValidWeaveInput(level, heldItem) && heldItem.getCount() >= 16)
                    {
                        ItemStack stack = ItemHandlerHelper.copyStackWithSize(heldItem, 16);
                        stack = ItemHandlerHelper.insertItem(insertionHandler, stack, false);
                        if (stack.isEmpty())
                        {
                            heldItem.shrink(16);
                            return true;
                        }
                    }
                    if (player.isShiftKeyDown())
                    {
                        ItemStack stack = master.inventory.get(11);
                        if (!stack.isEmpty())
                        {
                            player.getInventory().add(stack);
                            int size = stack.getCount();
                            stack.shrink(size);
                            return true;
                        }
                    }
                }
            }
            if (bX == 0 && bY == 0 && (bZ == 1 || bZ == 2 || bZ == 3))
            {
                IItemHandler outputHandler = output.getNullable();
                if (outputHandler != null)
                {
                    for (int i = 12; i < 14; i++)
                    {
                        if (player.isShiftKeyDown())
                        {
                            ItemStack stack = master.inventory.get(i);
                            if (!stack.isEmpty())
                            {
                                player.getInventory().add(stack);
                                int size = stack.getCount();
                                stack.shrink(size);
                                return true;
                            }
                        }
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
        registerCapability(new IEInventoryHandler(2, this, 12, false, true))
    );

    @Nullable
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

    @Nullable
    @Override
    public int[] getOutputTanks()
    {
        return null;
    }

    @Nullable
    @Override
    public int[] getOutputSlots()
    {
        return new int[] {12, 13};
    }

    @Override
    public PowerLoomBlockEntity getGuiMaster()
    {
        return master();
    }

    @Override
    public boolean canUseGui(Player player)
    {
        return formed && player.getMainHandItem().is(ATTItems.PIRN.get());
        // return false;
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
        if (slot >= 8 && slot < 12)
            return PowerLoomRecipe.isValidWeaveInput(level, stack);
        return false;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        if (slot >= 0 && slot < 8)
            return 1;
        if (slot == 11)
            return 16;
        return 64;
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<PowerLoomRecipe> process)
    {
        PowerLoomRecipe recipe = process.getRecipe(level);
        ItemStack stack = this.getInventory().get(11);
        if (recipe != null && !stack.isEmpty())
        {
            return recipe.inputs[0].testIgnoringSize(stack) && stack.getCount() == 16;
        }
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
        ItemStack stack = ATTItems.PIRN.get().getDefaultInstance();
        stack = Utils.insertStackIntoInventory(this.secondaryOutput, stack, false);
        if (!stack.isEmpty())
        {
            BlockPos pos = this.getBlockPosForPos(SECONDARY_OUT_POS).relative(getFacing().getClockWise(), 1);
            Utils.dropStackAtPos(level, pos, stack, getFacing().getClockWise());
        }
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
