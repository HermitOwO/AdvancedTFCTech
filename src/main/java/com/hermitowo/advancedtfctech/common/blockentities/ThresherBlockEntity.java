package com.hermitowo.advancedtfctech.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
import com.hermitowo.advancedtfctech.api.crafting.ThresherRecipe;
import com.hermitowo.advancedtfctech.common.blocks.ticking.ATTCommonTickableBlock;
import com.hermitowo.advancedtfctech.common.container.ATTContainerProvider;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.multiblocks.ThresherMultiblock;
import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
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
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;

public class ThresherBlockEntity extends PoweredMultiblockBlockEntity<ThresherBlockEntity, ThresherRecipe> implements ATTCommonTickableBlock, ATTContainerProvider<ThresherBlockEntity>, IEBlockInterfaces.IBlockBounds, IEBlockInterfaces.ISoundBE
{
    private static final BlockPos MAIN_OUT_POS = new BlockPos(1, 0, 0);
    private static final BlockPos SECONDARY_OUT_POS = new BlockPos(1, 1, 2);
    private static final int[] OUTPUT_SLOTS = new int[] {6, 7, 8, 9, 10, 11};

    public NonNullList<ItemStack> inventory = NonNullList.withSize(12, ItemStack.EMPTY);
    public List<ItemStack> outputList = this.getInventory().subList(6, 12);

    private final CapabilityReference<IItemHandler> output = CapabilityReference.forBlockEntityAt(this,
        () -> new DirectionalBlockPos(this.getBlockPosForPos(MAIN_OUT_POS).relative(getFacing()), getFacing().getOpposite()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    private final CapabilityReference<IItemHandler> secondaryOutput = CapabilityReference.forBlockEntityAt(this,
        () -> new DirectionalBlockPos(this.getBlockPosForPos(SECONDARY_OUT_POS).relative(getFacing(), -1), getFacing().getOpposite()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    public ThresherBlockEntity(BlockEntityType<ThresherBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(ThresherMultiblock.INSTANCE, 16000, true, type, pos, state);
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
        boolean active = shouldRenderAsActive();
        ImmersiveEngineering.proxy.handleTileSound(IESounds.crusher, this, active, .25f, 1);
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
        {
            return;
        }

        super.tickServer();
        boolean update = false;

        if (!isRSDisabled() && this.energyStorage.getEnergyStored() > 0)
        {
            if (this.processQueue.size() < this.getProcessQueueMaxLength())
            {
                final int[] usedInvSlots = new int[6];
                for (MultiblockProcess<ThresherRecipe> process : processQueue)
                {
                    if (process instanceof MultiblockProcessInMachine)
                    {
                        for (int i : ((MultiblockProcessInMachine<ThresherRecipe>) process).getInputSlots())
                        {
                            usedInvSlots[i]++;
                        }
                    }
                }
                for (int slot = 0; slot < 6; slot++)
                {
                    ItemStack stack = this.getInventory().get(slot);
                    if (!stack.isEmpty())
                    {
                        stack = stack.copy();
                        stack.shrink(usedInvSlots[slot]);
                    }
                    if (!stack.isEmpty() && stack.getCount() > 0)
                    {
                        ThresherRecipe recipe = ThresherRecipe.findRecipe(level, stack);
                        if (recipe != null)
                        {
                            MultiblockProcessInMachine<ThresherRecipe> process = new MultiblockProcessInMachine<>(recipe, this::getRecipeForId, slot);
                            if (this.addProcessToQueue(process, true))
                            {
                                this.addProcessToQueue(process, false);
                                update = true;
                            }
                        }
                    }
                }
            }
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
                IFood cap1 = holder1.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
                IFood cap2 = holder2.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
                int size1 = holder1.getCount();
                int size2 = holder2.getCount();
                int sizeMax = holder1.getMaxStackSize();
                if (!holder1.isEmpty() && size1 < sizeMax && !holder2.isEmpty() && size2 < sizeMax && holder1.is(holder2.getItem()) && cap1.getCreationDate() == cap2.getCreationDate())
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

    @Override
    public Set<MultiblockFace> getEnergyPos()
    {
        return ImmutableSet.of(new MultiblockFace(2, 1, 1, RelativeBlockFace.RIGHT));
    }

    @Override
    public Set<BlockPos> getRedstonePos()
    {
        return ImmutableSet.of(new BlockPos(0, 1, 1));
    }

    private final MultiblockCapability<IItemHandler> insertionHandler = MultiblockCapability.make(
        this, be -> be.insertionHandler, ThresherBlockEntity::master,
        registerCapability(new IEInventoryHandler(6, this, 0, new boolean[] {true, true, true, true, true, true}, new boolean[6]))
    );
    private final MultiblockCapability<IItemHandler> outputHandler = MultiblockCapability.make(
        this, be -> be.outputHandler, ThresherBlockEntity::master,
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
    protected ThresherRecipe getRecipeForId(Level level, ResourceLocation id)
    {
        return ThresherRecipe.RECIPES.getById(level, id);
    }

    @Override
    public ThresherRecipe findRecipeForInsertion(ItemStack inserting)
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
    public ThresherBlockEntity getGuiMaster()
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
    public BEContainerATT<? super ThresherBlockEntity, ?> getContainerTypeATT()
    {
        return ATTContainerTypes.THRESHER;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack)
    {
        return ThresherRecipe.isValidRecipeInput(level, stack);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<ThresherRecipe> process)
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
    public void onProcessFinish(MultiblockProcess<ThresherRecipe> process)
    {
        ThresherRecipe recipe = process.getRecipe(level);
        for (Lazy<ItemStack> out : recipe.secondaryOutputs)
        {
            ItemStack stack = out.get();
            stack = Utils.insertStackIntoInventory(this.secondaryOutput, stack, false);
            if (!stack.isEmpty())
            {
                BlockPos pos = getBlockPos().offset(0, 1, 0).relative(getFacing(), -2);
                Utils.dropStackAtPos(level, pos, stack, getFacing().getOpposite());
            }
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
    public float getMinProcessDistance(MultiblockProcess<ThresherRecipe> process)
    {
        return 0;
    }

    @Override
    public boolean isInWorldProcessingMachine()
    {
        return false;
    }

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES = CachedShapesWithTransform.createForMultiblock(ThresherBlockEntity::getShape);

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
            if (!(bX == 1 && bZ == 0))
                main.add(new AABB(0, 0, 0, 1, .5, 1));

            if (bX == 0 && bZ == 1) // Redstone Input Legs
            {
                main.add(new AABB(.125, 0, .75, .375, 1, .875));
                main.add(new AABB(.125, 0, .125, .375, 1, .25));
            }
        }

        // Second Layer
        if (bY == 1)
        {
            if (bX == 0 && bZ == 0)
                main.add(new AABB(.5, 0, .5625, 1, .875, 1));
            if (bX == 0 && bZ == 2)
                main.add(new AABB(.5, 0, 0, 1, .875, .4375));
            if (bX == 2 && bZ == 0)
                main.add(new AABB(0, 0, .5625, .5, .875, 1));
            if (bX == 2 && bZ == 2)
                main.add(new AABB(0, 0, 0, .5, .875, .4375));

            if (bX == 1)
            {
                if (bZ == 0)
                    main.add(new AABB(0, 0, .5625, 1, .875, 1));

                if (bZ == 2)
                {
                    main.add(new AABB(.125, .125, .4375, .875, .875, 1));
                    main.add(new AABB(0, 0, 0, 1, .875, .4375));
                }
            }
        }

        // Top
        if (bY == 2 && bZ == 1)
        {
            if (bX == 0)
                main.add(new AABB(.5, 0, 0.125, 1, .4375, .875));
            if (bX == 2)
                main.add(new AABB(0, 0, 0.125, .5, .4375, .875));
        }

        if (main.isEmpty())
            main.add(new AABB(0, 0, 0, 1, 1, 1));
        return main;
    }
}