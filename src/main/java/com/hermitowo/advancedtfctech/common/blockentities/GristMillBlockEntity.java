package com.hermitowo.advancedtfctech.common.blockentities;

import java.util.ArrayList;
import java.util.List;
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
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.orientation.RelativeBlockFace;
import com.google.common.collect.ImmutableSet;
import com.hermitowo.advancedtfctech.api.crafting.GristMillRecipe;
import com.hermitowo.advancedtfctech.common.blocks.ticking.ATTCommonTickableBlock;
import com.hermitowo.advancedtfctech.common.container.ATTContainerProvider;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.multiblocks.GristMillMultiblock;
import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
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

public class GristMillBlockEntity extends PoweredMultiblockBlockEntity<GristMillBlockEntity, GristMillRecipe> implements ATTCommonTickableBlock, ATTContainerProvider<GristMillBlockEntity>, IEBlockInterfaces.IBlockBounds, IEBlockInterfaces.ISoundBE
{
    private static final BlockPos MAIN_OUT_POS = new BlockPos(1, 0, 2);
    private static final int[] OUTPUT_SLOTS = new int[] {6, 7, 8, 9, 10, 11};

    public NonNullList<ItemStack> inventory = NonNullList.withSize(12, ItemStack.EMPTY);
    public List<ItemStack> outputList = inventory.subList(6, 12);

    private final CapabilityReference<IItemHandler> output = CapabilityReference.forBlockEntityAt(this,
        () -> new DirectionalBlockPos(this.getBlockPosForPos(MAIN_OUT_POS).relative(getFacing(), -1), getFacing().getOpposite()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    public GristMillBlockEntity(BlockEntityType<GristMillBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(GristMillMultiblock.INSTANCE, 16000, true, type, pos, state);
    }

    public float animation_driverRotation = 0;

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
        boolean active = shouldRenderAsActive();
        ImmersiveEngineering.proxy.handleTileSound(IESounds.crusher, this, active, .25f, 1);
        if (active)
        {
            animation_driverRotation += 18f;
            animation_driverRotation %= 360f;
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
            if (this.processQueue.size() < this.getProcessQueueMaxLength())
            {
                final int[] usedInvSlots = new int[6];
                for (MultiblockProcess<GristMillRecipe> process : processQueue)
                {
                    if (process instanceof MultiblockProcessInMachine)
                    {
                        for (int i : ((MultiblockProcessInMachine<GristMillRecipe>) process).getInputSlots())
                        {
                            usedInvSlots[i]++;
                        }
                    }
                }
                for (int slot = 0; slot < 6; slot++)
                {
                    ItemStack stack = inventory.get(slot);
                    if (!stack.isEmpty())
                    {
                        stack = stack.copy();
                        stack.shrink(usedInvSlots[slot]);
                    }
                    if (!stack.isEmpty() && stack.getCount() > 0)
                    {
                        GristMillRecipe recipe = GristMillRecipe.findRecipe(level, stack);
                        if (recipe != null)
                        {
                            MultiblockProcessGristMill process = new MultiblockProcessGristMill(recipe, this::getRecipeForId, slot);
                            if (this.addProcessToQueue(process, true))
                            {
                                this.addProcessToQueue(process, false);
                                update = true;
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

    public void sort()
    {
        for (int i = 0; i < outputList.size(); i++)
        {
            for (int j = i + 1; j < outputList.size(); j++)
            {
                ItemStack holder1 = outputList.get(i);
                ItemStack holder2 = outputList.get(j);
                int size1 = holder1.getCount();
                int size2 = holder2.getCount();
                int sizeMax = holder1.getMaxStackSize();
                if (!holder1.isEmpty() && size1 < sizeMax && !holder2.isEmpty() && size2 < sizeMax && holder1.is(holder2.getItem()))
                {
                    if (Utils.compareItemNBT(holder1, holder2))
                    {
                        if (size1 + size2 > sizeMax)
                        {
                            if (size1 >= size2)
                            {
                                int amount = sizeMax - size2;
                                this.inventory.get(i + 6).shrink(amount);
                                this.inventory.get(j + 6).grow(amount);
                            }
                            else
                            {
                                int amount = sizeMax - size1;
                                this.inventory.get(i + 6).grow(amount);
                                this.inventory.get(j + 6).shrink(amount);
                            }
                        }
                        else
                        {
                            ItemStack stack = new ItemStack(outputList.get(j).getItem(), outputList.get(i).getCount() + outputList.get(j).getCount());
                            this.inventory.set(i + 6, stack);
                            this.inventory.set(j + 6, ItemStack.EMPTY);
                        }
                        sort();
                    }
                }
            }
        }
    }

    @Override
    public Set<MultiblockFace> getEnergyPos()
    {
        return ImmutableSet.of(new MultiblockFace(3, 1, 1, RelativeBlockFace.RIGHT));
    }

    @Override
    public Set<BlockPos> getRedstonePos()
    {
        return ImmutableSet.of(new BlockPos(1, 1, 0));
    }

    private final MultiblockCapability<IItemHandler> insertionHandler = MultiblockCapability.make(
        this, be -> be.insertionHandler, GristMillBlockEntity::master,
        registerCapability(new IEInventoryHandler(6, this, 0, true, false))
    );
    private final MultiblockCapability<IItemHandler> outputHandler = MultiblockCapability.make(
        this, be -> be.outputHandler, GristMillBlockEntity::master,
        registerCapability(new IEInventoryHandler(6, this, 6, false, true))
    );

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (posInMultiblock == MAIN_OUT_POS)
                return outputHandler.getAndCast();
            if (new BlockPos(1, 2, 1).equals(posInMultiblock) && facing == Direction.UP)
                return insertionHandler.getAndCast();
        }
        return super.getCapability(capability, facing);
    }

    @Nullable
    @Override
    protected GristMillRecipe getRecipeForId(Level level, ResourceLocation id)
    {
        return GristMillRecipe.RECIPES.getById(level, id);
    }

    @Override
    public GristMillRecipe findRecipeForInsertion(ItemStack inserting)
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
        return OUTPUT_SLOTS;
    }

    @Override
    public GristMillBlockEntity getGuiMaster()
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

    @NotNull
    @Override
    public BEContainerATT<? super GristMillBlockEntity, ?> getContainerTypeATT()
    {
        return ATTContainerTypes.GRIST_MILL;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack)
    {
        return GristMillRecipe.isValidRecipeInput(level, stack);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<GristMillRecipe> process)
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
    public void onProcessFinish(MultiblockProcess<GristMillRecipe> process)
    {
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
    public float getMinProcessDistance(MultiblockProcess<GristMillRecipe> process)
    {
        return 0;
    }

    @Override
    public boolean isInWorldProcessingMachine()
    {
        return false;
    }

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(GristMillBlockEntity::getShape);

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
            if ((bZ == 2 && !(bX == 1)) || bZ == 0)
            {
                main.add(new AABB(0, 0, 0, 1, .5, 1));
            }

            if (bZ == 1 && bX == 2)
            {
                main.add(box(0, 0, 0, 16, 8, 16));
                main.add(box(0, 8, 0, 4, 16, 16));
            }

            if (bZ == 0)
            {
                if (bX == 1)
                {
                    main.add(box(0, 8, 8, 16, 16, 16));
                    main.add(box(2, 8, 2, 4, 16, 6));
                    main.add(box(12, 8, 2, 14, 16, 6));
                }
                if (bX == 2)
                {
                    main.add(box(0, 8, 8, 4, 16, 16));
                    main.add(box(4, 8, 11.5, 16, 16, 15.5));
                }
                if (bX == 3)
                {
                    main.add(box(1, 8, 8, 5, 16, 16));
                    main.add(box(0, 8, 11.5, 1, 16, 15.5));
                }
            }
            if (bZ == 2)
            {
                if (bX == 2)
                {
                    main.add(box(0, 8, 0, 4, 16, 8));
                    main.add(box(4, 8, .5, 16, 16, 4.5));
                }
                if (bX == 3)
                {
                    main.add(box(1, 8, 0, 5, 16, 8));
                    main.add(box(0, 8, .5, 1, 16, 4.5));
                }
            }
        }

        //Second Layer
        if (bY == 1)
        {
            if (bZ == 1)
            {
                if (bX == 0)
                {
                    main.add(box(0, 0, 0, 16, 4, 16));
                    main.add(box(1, 4, .5, 15, 12, 15.5));
                }
                if (bX == 2)
                {
                    main.add(box(0, 0, 0, 4, 4, 16));
                    main.add(box(0, 5, 7, 16, 7, 9));
                }
                if (bX == 3)
                {
                    main.add(box(0, 0, 0, 16, 4, 16));
                    main.add(box(1, 4, .5, 15, 12, 15.5));
                    main.add(box(15, 5, 5, 16, 11, 11));
                }
            }
            if (bZ == 0)
            {
                if (bX == 2)
                {
                    main.add(box(0, 0, 8, 4, 4, 16));
                    main.add(box(4, 0, 11.5, 16, 4, 15.5));
                }
                if (bX == 3)
                {
                    main.add(box(1, 0, 8, 5, 4, 16));
                    main.add(box(0, 0, 11.5, 1, 4, 15.5));
                }
            }
            if (bZ == 2)
            {
                if (bX == 1)
                {
                    main.add(box(0, 0, 0, 16, 16, 8));
                }
                if (bX == 2)
                {
                    main.add(box(0, 0, 0, 4, 4, 8));
                    main.add(box(4, 0, .5, 16, 4, 4.5));
                }
                if (bX == 3)
                {
                    main.add(box(1, 0, 0, 5, 4, 8));
                    main.add(box(0, 0, .5, 1, 4, 4.5));
                }
            }
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

    public static class MultiblockProcessGristMill extends MultiblockProcessInMachine<GristMillRecipe>
    {
        public MultiblockProcessGristMill(GristMillRecipe recipe, BiFunction<Level, ResourceLocation, GristMillRecipe> getRecipe, int... inputSlots)
        {
            super(recipe, getRecipe, inputSlots);
        }

        @Override
        protected NonNullList<ItemStack> getRecipeItemOutputs(PoweredMultiblockBlockEntity<?, GristMillRecipe> multiblock)
        {
            GristMillRecipe recipe = getRecipe(multiblock.getLevel());
            if (recipe == null)
                return NonNullList.create();
            assert multiblock.getInventory() != null;
            ItemStack input = multiblock.getInventory().get(this.inputSlots[0]);
            return recipe.generateActualOutput(input);
        }
    }
}
