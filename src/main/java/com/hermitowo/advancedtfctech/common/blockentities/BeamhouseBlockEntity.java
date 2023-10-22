package com.hermitowo.advancedtfctech.common.blockentities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.orientation.RelativeBlockFace;
import com.google.common.collect.ImmutableSet;
import com.hermitowo.advancedtfctech.client.ATTSounds;
import com.hermitowo.advancedtfctech.common.blocks.ticking.ATTCommonTickableBlock;
import com.hermitowo.advancedtfctech.common.container.ATTContainerProvider;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.multiblocks.BeamhouseMultiblock;
import com.hermitowo.advancedtfctech.common.recipes.BeamhouseRecipe;
import com.hermitowo.advancedtfctech.util.FluidHelper;
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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class BeamhouseBlockEntity extends PoweredMultiblockBlockEntity<BeamhouseBlockEntity, BeamhouseRecipe> implements ATTCommonTickableBlock, ATTContainerProvider<BeamhouseBlockEntity>, IEBlockInterfaces.IBlockBounds, IEBlockInterfaces.IPlayerInteraction, IEBlockInterfaces.ISoundBE
{
    private static final BlockPos IN_POS = new BlockPos(3, 0, 1);
    private static final BlockPos OUT_POS = new BlockPos(3, 0, 0);
    private static final MultiblockFace FLUID_INPUT = new MultiblockFace(2, 0, 3, RelativeBlockFace.FRONT);
    private static final int[] OUTPUT_SLOTS = new int[] {12, 13, 14};

    public NonNullList<ItemStack> inventory = NonNullList.withSize(17, ItemStack.EMPTY);
    public final FluidTank tank = new FluidTank(24000);

    private final CapabilityReference<IItemHandler> output = CapabilityReference.forBlockEntityAt(this,
        () -> new DirectionalBlockPos(this.getBlockPosForPos(OUT_POS).relative(getFacing()), getFacing().getOpposite()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    public BeamhouseBlockEntity(BlockEntityType<BeamhouseBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(BeamhouseMultiblock.INSTANCE, 32000, true, type, pos, state);
    }

    public float barrelRotation = 0;

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        tank.readFromNBT(nbt.getCompound("tank"));
        if(!descPacket)
            ContainerHelper.loadAllItems(nbt, inventory);
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        CompoundTag tankTag = tank.writeToNBT(new CompoundTag());
        nbt.put("tank", tankTag);
        if(!descPacket)
            ContainerHelper.saveAllItems(nbt, inventory);
    }

    @Override
    public void tickClient()
    {
        boolean active = shouldRenderAsActive();
        ImmersiveEngineering.proxy.handleTileSound(ATTSounds.BEAMHOUSE.get(), this, active, 1, 1);
        if (active)
        {
            barrelRotation += 18f;
            barrelRotation %= 360f;
        }
    }

    @Override
    public boolean shouldPlaySound(String sound)
    {
        return shouldRenderAsActive();
    }

    @Override
    public void tickServer()
    {
        if (isDummy())
            return;

        super.tickServer();
        boolean update = false;

        if (!isRSDisabled() && this.energyStorage.getEnergyStored() > 0)
        {
            assert level != null;
            if (this.processQueue.size() < this.getProcessQueueMaxLength())
            {
                Map<Integer, Integer> usedInvSlots = new HashMap<>();
                for (MultiblockProcess<BeamhouseRecipe> process : processQueue)
                {
                    if (process instanceof MultiblockProcessInMachine)
                    {
                        int[] inputSlots = ((MultiblockProcessInMachine<BeamhouseRecipe>) process).getInputSlots();
                        int[] inputAmounts = ((MultiblockProcessInMachine<BeamhouseRecipe>) process).getInputAmounts();
                        if (inputAmounts != null)
                        {
                            for (int i = 0; i < inputSlots.length; i++)
                            {
                                if (inputAmounts[i] > 0)
                                {
                                    if (usedInvSlots.containsKey(inputSlots[i]))
                                        usedInvSlots.put(inputSlots[i], usedInvSlots.get(inputSlots[i]) + inputAmounts[i]);
                                    else
                                        usedInvSlots.put(inputSlots[i], inputAmounts[i]);
                                }
                            }
                        }
                    }
                }
                for (int slot = 0; slot < 12; slot++)
                {
                    if (!usedInvSlots.containsKey(slot))
                    {
                        ItemStack stack = inventory.get(slot);
                        if (!stack.isEmpty() && stack.getCount() > 0 && tank.getFluidAmount() > 0)
                        {
                            BeamhouseRecipe recipe = BeamhouseRecipe.findRecipe(level, stack, tank.getFluid());
                            if (recipe != null)
                            {
                                int fluidAmount = 0;
                                for (MultiblockProcess<BeamhouseRecipe> processInQueue : processQueue)
                                {
                                    BeamhouseRecipe recipeInQueue = processInQueue.getRecipe(level);
                                    if (recipeInQueue != null)
                                        fluidAmount += recipeInQueue.fluidInput.getAmount();
                                }
                                fluidAmount += recipe.fluidInput.getAmount();
                                if (tank.getFluidAmount() >= fluidAmount)
                                {
                                    MultiblockProcessBeamhouse process = new MultiblockProcessBeamhouse(recipe, this::getRecipeForId, slot).setInputTanks(0);
                                    if (this.addProcessToQueue(process, true))
                                    {
                                        this.addProcessToQueue(process, false);
                                        process.setInputAmounts(recipe.input.getCount());
                                        update = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (FluidHelper.drainFluidContainer(inventory, tank, 15 ,16))
                update = true;

            if (level.getGameTime() % 8 == 0)
            {
                IItemHandler outputHandler = output.getNullable();
                if (outputHandler != null)
                {
                    for (int j : OUTPUT_SLOTS)
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
        BeamhouseBlockEntity master = master();
        if (master != null)
        {
            if (posInMultiblock.getX() == 2 && posInMultiblock.getY() == 0 && posInMultiblock.getZ() == 3)
            {
                if (FluidHelper.interactWithFluidHandler(player, hand, master.tank, master.processQueue.isEmpty()))
                {
                    this.updateMasterBlock(null, true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<MultiblockFace> getEnergyPos()
    {
        return ImmutableSet.of(new MultiblockFace(0, 1, 3, RelativeBlockFace.UP));
    }

    @Override
    public Set<BlockPos> getRedstonePos()
    {
        return ImmutableSet.of(new BlockPos(3, 1, 2));
    }

    private final MultiblockCapability<IItemHandler> inputHandler = MultiblockCapability.make(
        this, be -> be.inputHandler, BeamhouseBlockEntity::master,
        registerCapability(new IEInventoryHandler(12, this, 0, true, false)
        {
            //ignore the given slot and spread it out
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
            {
                if (stack.isEmpty())
                    return stack;
                stack = stack.copy();
                List<Integer> possibleSlots = new ArrayList<>(12);
                for (int i = 0; i < 12; i++)
                {
                    ItemStack here = inventory.get(i);
                    if (here.isEmpty() && isStackValid(i, stack))
                    {
                        if (!simulate)
                            inventory.set(i, stack);
                        return ItemStack.EMPTY;
                    }
                    else if (ItemHandlerHelper.canItemStacksStack(stack, here) && here.getCount() < here.getMaxStackSize())
                    {
                        possibleSlots.add(i);
                    }
                }
                possibleSlots.sort(Comparator.comparingInt(a -> inventory.get(a).getCount()));
                for (int i : possibleSlots)
                {
                    ItemStack here = inventory.get(i);
                    int fillCount = Math.min(here.getMaxStackSize() - here.getCount(), stack.getCount());
                    if (!simulate)
                        here.grow(fillCount);
                    stack.shrink(fillCount);
                    if (stack.isEmpty())
                        return ItemStack.EMPTY;
                }
                return stack;
            }
        })
    );

    private final MultiblockCapability<IItemHandler> outputHandler = MultiblockCapability.make(
        this, be -> be.outputHandler, BeamhouseBlockEntity::master,
        registerCapability(new IEInventoryHandler(3, this, 12, false, true))
    );

    private final MultiblockCapability<IFluidHandler> fluidInputHandler = MultiblockCapability.make(
        this, be -> be.fluidInputHandler, BeamhouseBlockEntity::master, registerFluidInput(tank)
    );

    private final MultiblockCapability<IFluidHandler> allFluids = MultiblockCapability.make(
        this, be -> be.allFluids, BeamhouseBlockEntity::master, registerFluidView(tank)
    );

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            if (facing == null || FLUID_INPUT.equals(asRelativeFace(facing)))
                return fluidInputHandler.getAndCast();
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (posInMultiblock.equals(IN_POS))
                return inputHandler.getAndCast();
            if (posInMultiblock.equals(OUT_POS))
                return outputHandler.getAndCast();
        }
        return super.getCapability(capability, facing);
    }

    @Nullable
    @Override
    protected BeamhouseRecipe getRecipeForId(Level level, ResourceLocation id)
    {
        return BeamhouseRecipe.RECIPES.getById(level, id);
    }

    @Nullable
    @Override
    public BeamhouseRecipe findRecipeForInsertion(ItemStack inserting)
    {
        return null;
    }

    @Nullable
    @Override
    public IFluidTank[] getInternalTanks()
    {
        return new IFluidTank[]{tank};
    }

    @Override
    public int[] getOutputTanks()
    {
        return new int[0];
    }

    @Override
    public int[] getOutputSlots()
    {
        return OUTPUT_SLOTS;
    }

    @Nullable
    @Override
    public BeamhouseBlockEntity getGuiMaster()
    {
        return master();
    }

    @Override
    public boolean canUseGui(Player player)
    {
        return formed;
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory()
    {
        return this.inventory;
    }

    @Nonnull
    @Override
    public BEContainerATT<? super BeamhouseBlockEntity, ?> getContainerTypeATT()
    {
        return ATTContainerTypes.BEAMHOUSE;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack)
    {
        assert level != null;
        return BeamhouseRecipe.isValidRecipeInput(level, stack);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<BeamhouseRecipe> process)
    {
        return true;
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
    public void onProcessFinish(MultiblockProcess<BeamhouseRecipe> process)
    {
    }

    @Override
    public int getMaxProcessPerTick()
    {
        return 12;
    }

    @Override
    public int getProcessQueueMaxLength()
    {
        return 12;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<BeamhouseRecipe> process)
    {
        return 0;
    }

    @Override
    public boolean isInWorldProcessingMachine()
    {
        return false;
    }

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(BeamhouseBlockEntity::getShape);

    @Nonnull
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
            if (bX < 3 && bZ < 3)
                main.add(box(0, 0, 0, 16, 8, 16));
            if (bZ == 3)
            {
                if (bX == 1 || bX == 3)
                    main.add(box(0, 0, 0, 16, 8, 16));
                if (bX == 1)
                    main.add(box(8, 8, 0, 16, 16, 16));
                if (bX == 3)
                    main.add(box(0, 8, 0, 8, 16, 16));
            }
            if (bX == 3 && bZ == 2)
            {
                main.add(box(0, 0, 0, 16, 8, 16));
                main.add(box(10, 8, 2, 14, 16, 4));
                main.add(box(10, 8, 12, 14, 16, 14));
            }
        }

        // Second Layer
        if (bY == 1)
        {
            if (bZ == 0)
            {
                if (bX == 0)
                    main.add(box(8, 6, 9, 16, 18, 16));
                if (bX == 1 || bX == 2)
                    main.add(box(0, 6, 9, 16, 18, 16));
            }
            if (bZ == 2)
            {
                if (bX == 0)
                {
                    main.add(box(8, 6, 0, 16, 18, 7));
                    main.add(box(6, 8, 11, 16, 12, 15));
                    main.add(box(6, 8, 15, 10, 12, 16));
                }
                if (bX == 1)
                {
                    main.add(box(0, 6, 0, 16, 18, 7));
                    main.add(box(4, 4, 10, 12, 16, 16));
                    main.add(box(0, 8, 11, 4, 12, 15));
                }
                if (bX == 2)
                    main.add(box(0, 6, 0, 16, 18, 7));
                if (bX == 3)
                    main.add(box(8, 0, 0, 16, 16, 16));
            }
            if (bZ == 1)
            {
                if (bX == 0)
                {
                    main.add(box(8, 6, 0, 16, 18, 16));
                    main.add(box(6, 7, 3, 8, 17, 13));
                }
                if (bX == 2)
                    main.add(box(0, 6, 0, 16, 18, 16));
            }
            if (bZ == 3 && bX == 2)
                main.add(box(-8, 0, 0, 24, 8, 16));
        }

        // Top Layer
        if (bY == 2)
        {
            if (bZ == 1)
            {
                if (bX == 0)
                    main.add(box(8, 2, 2, 16, 11, 14));
                if (bX == 1 || bX == 2)
                    main.add(box(0, 2, 2, 16, 11, 14));
            }
            else
                main.add(box(0, 0, 0, 0, 0, 0));
        }

        if (main.isEmpty())
            main.add(new AABB(0, 0, 0, 1, 1, 1));
        return main;
    }

    // Pixel-based
    private static AABB box(double x0, double y0, double z0, double x1, double y1, double z1)
    {
        return new AABB(x0 / 16D, y0 / 16D, z0 / 16D, x1 / 16D, y1 / 16D, z1 / 16D);
    }

    @Override
    @Nullable
    protected MultiblockProcess<BeamhouseRecipe> loadProcessFromNBT(CompoundTag tag)
    {
        ResourceLocation id = new ResourceLocation(tag.getString("recipe"));
        MultiblockProcessBeamhouse process = new MultiblockProcessBeamhouse(id, this::getRecipeForId, tag.getIntArray("process_inputSlots"));
        if (tag.contains("process_inputAmounts", Tag.TAG_INT_ARRAY))
            process.setInputAmounts(tag.getIntArray("process_inputAmounts"));
        return process;
    }

    public static class MultiblockProcessBeamhouse extends MultiblockProcessInMachine<BeamhouseRecipe>
    {
        public MultiblockProcessBeamhouse(ResourceLocation id, BiFunction<Level, ResourceLocation, BeamhouseRecipe> getRecipe, int... inputSlots)
        {
            super(id, getRecipe, inputSlots);
        }

        public MultiblockProcessBeamhouse(BeamhouseRecipe recipe, BiFunction<Level, ResourceLocation, BeamhouseRecipe> getRecipe, int... inputSlots)
        {
            super(recipe, getRecipe, inputSlots);
        }

        @Override
        public MultiblockProcessBeamhouse setInputTanks(int... inputTanks)
        {
            this.inputTanks = inputTanks;
            return this;
        }

        @Override
        protected NonNullList<ItemStack> getRecipeItemOutputs(PoweredMultiblockBlockEntity<?, BeamhouseRecipe> multiblock)
        {
            BeamhouseRecipe recipe = getRecipe(multiblock.getLevel());
            if (recipe == null)
                return NonNullList.create();
            assert multiblock.getInventory() != null;
            ItemStack input = multiblock.getInventory().get(this.inputSlots[0]);
            return recipe.generateActualOutput(input);
        }
    }
}
