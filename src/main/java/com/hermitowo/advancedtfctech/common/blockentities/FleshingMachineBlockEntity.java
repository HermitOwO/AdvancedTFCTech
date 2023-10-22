package com.hermitowo.advancedtfctech.common.blockentities;

import java.util.Collections;
import java.util.function.Supplier;
import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.api.energy.MutableEnergyStorage;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.PlacementLimitation;
import blusunrize.immersiveengineering.common.blocks.ticking.IEClientTickableBE;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.util.CachedRecipe;
import blusunrize.immersiveengineering.common.util.EnergyHelper;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.hermitowo.advancedtfctech.client.ATTSounds;
import com.hermitowo.advancedtfctech.common.blocks.metal.FleshingMachineBlock;
import com.hermitowo.advancedtfctech.common.container.ATTContainerProvider;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.items.ATTItems;
import com.hermitowo.advancedtfctech.common.recipes.FleshingMachineRecipe;
import com.hermitowo.advancedtfctech.config.ATTConfig;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class FleshingMachineBlockEntity extends IEBaseBlockEntity implements IEServerTickableBE, IEClientTickableBE, ATTContainerProvider<FleshingMachineBlockEntity>, IIEInventory, IEBlockInterfaces.IActiveState, IEBlockInterfaces.IProcessBE, IEBlockInterfaces.IPlayerInteraction, IEBlockInterfaces.IStateBasedDirectional, IEBlockInterfaces.IHasDummyBlocks, IEBlockInterfaces.ISoundBE, IModelOffsetProvider
{
    public static final BlockPos MASTER_POS = BlockPos.ZERO;
    public static final BlockPos DUMMY_POS = new BlockPos(1, 0, 0);

    public static final int INPUT_SLOT = 0;
    public static final int BLADE_SLOT = 1;

    public float animation_bladeRotation = 0;
    public float animation_rodRotation = 0;

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    public MutableEnergyStorage energyStorage = new MutableEnergyStorage(16000);
    private final MultiblockCapability<IEnergyStorage> energyCap = MultiblockCapability.make(
        this, be -> be.energyCap, FleshingMachineBlockEntity::master, registerEnergyInput(energyStorage)
    );

    private final CapabilityReference<IItemHandler> output = CapabilityReference.forBlockEntityAt(this,
        () -> new DirectionalBlockPos(getBlockPos().relative(getFacing(), -1), getFacing().getOpposite()),
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);

    public final Supplier<FleshingMachineRecipe> cachedRecipe = CachedRecipe.cached(
        FleshingMachineRecipe::findRecipe, () -> level, () -> inventory.get(INPUT_SLOT)
    );

    public int process = 0;
    public int processMax = 0;

    public FleshingMachineBlockEntity(BlockEntityType<FleshingMachineBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        Collections.fill(inventory, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, inventory);
        EnergyHelper.deserializeFrom(energyStorage, nbt);
        process = nbt.getInt("process");
        processMax = nbt.getInt("processMax");
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        ContainerHelper.saveAllItems(nbt, inventory);
        EnergyHelper.serializeTo(energyStorage, nbt);
        nbt.putInt("process", process);
        nbt.putInt("processMax", processMax);
    }

    @Override
    public void tickClient()
    {
        if (getIsActive())
        {
            animation_bladeRotation += 36F;
            animation_bladeRotation %= 360F;

            animation_rodRotation += 9F;
            animation_rodRotation %= 360F;

            ImmersiveEngineering.proxy.handleTileSound(ATTSounds.FLESHING_MACHINE.get(), this, !inventory.get(BLADE_SLOT).isEmpty(), .2f, 1);
        }
    }

    @Override
    public boolean shouldPlaySound(String sound)
    {
        return getIsActive();
    }

    @Override
    public void tickServer()
    {
        ItemStack input = inventory.get(INPUT_SLOT);
        ItemStack blade = inventory.get(BLADE_SLOT);
        FleshingMachineRecipe recipe = cachedRecipe.get();
        boolean activeBeforeTick = getIsActive();
        if (process > 0)
        {
            if (input.isEmpty())
            {
                process = 0;
                processMax = 0;
            }
            else
            {
                if (recipe == null || recipe.time != processMax || blade.isEmpty())
                {
                    process = 0;
                    processMax = 0;
                    setActive(false);
                }
                else
                {
                    int consumption = recipe.energy / recipe.time;
                    if (energyStorage.extractEnergy(consumption, true) == consumption)
                    {
                        energyStorage.extractEnergy(consumption, false);
                        process--;
                        if (blade.hurt(1, Utils.RAND, null))
                            inventory.set(BLADE_SLOT, ItemStack.EMPTY);
                    }
                }
            }
            setChanged();
        }
        else
        {
            if (activeBeforeTick)
            {
                if (recipe != null)
                {
                    ItemStack output = recipe.output.getStack(input);
                    inventory.set(INPUT_SLOT, output);
                }
                processMax = 0;
                setActive(false);
            }
            if (recipe != null && !blade.isEmpty())
            {
                int consumption = recipe.energy / recipe.time;
                if (energyStorage.extractEnergy(consumption, true) == consumption)
                {
                    this.process = recipe.time;
                    this.processMax = process;
                    setActive(true);
                }
            }
        }
        final boolean activeAfterTick = getIsActive();
        if (activeBeforeTick != activeAfterTick)
        {
            setChanged();
            BlockEntity be = Utils.getExistingTileEntity(level, new BlockPos(1, 0, 0));
            if (be instanceof FleshingMachineBlockEntity)
                ((FleshingMachineBlockEntity) be).setActive(activeAfterTick);
        }

        if (getIsActive() && recipe != null)
            spawnParticles(recipe.input.getItems()[0], getFacing());

        assert level != null;
        if (level.getGameTime() % 8 == 0)
        {
            IItemHandler outputHandler = output.getNullable();
            if (outputHandler != null)
            {
                if (!FleshingMachineRecipe.isValidRecipeInput(level, input))
                {
                    ItemStack stack = ItemHandlerHelper.copyStackWithSize(input, 1);
                    stack = ItemHandlerHelper.insertItem(outputHandler, stack, false);
                    if (stack.isEmpty())
                    {
                        input.shrink(1);
                        if (input.getCount() <= 0)
                        {
                            inventory.set(INPUT_SLOT, ItemStack.EMPTY);
                            this.setChanged();
                            this.markContainingBlockForUpdate(null);
                        }
                    }
                }
            }
        }
    }

    private void spawnParticles(ItemStack stack, Direction facing)
    {
        if (facing == Direction.EAST)
        {
           spawnParticles(stack, facing, 0.45, 0.708);
           spawnParticles(stack, facing, 0.45, 1.167);
        }
        else if (facing == Direction.SOUTH)
        {
            spawnParticles(stack, facing, 0.292, 0.45);
            spawnParticles(stack, facing, -0.167, 0.45);
        }
        else if (facing == Direction.WEST)
        {
            spawnParticles(stack, facing, 0.55, 0.292);
            spawnParticles(stack, facing, 0.55, -0.167);
        }
        else
        {
            spawnParticles(stack, facing, 0.708, 0.55);
            spawnParticles(stack, facing, 1.167, 0.55);
        }
    }

    private void spawnParticles(ItemStack stack, Direction facing, double x, double z)
    {
        boolean facingX = facing.getAxis().equals(Axis.X);
        if (getLevel() instanceof ServerLevel serverLevel)
        {
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack),
                getBlockPos().getX() + x, getBlockPos().getY() + 0.65, getBlockPos().getZ() + z,
                1, facingX ? 0 : 0.2, 0, facingX ? 0.2 : 0, 0.03125);
        }
    }

    @Override
    public boolean interact(Direction side, Player player, InteractionHand hand, ItemStack heldItem, float hitX, float hitY, float hitZ)
    {
        FleshingMachineBlockEntity master = master();
        if (master != null)
        {
            if (side.equals(getFacing().getOpposite()) || side == Direction.UP)
            {
                assert level != null;
                ItemStack input = master.inventory.get(INPUT_SLOT);
                ItemStack blade = master.inventory.get(BLADE_SLOT);
                FleshingMachineRecipe recipe = FleshingMachineRecipe.findRecipe(level, heldItem);
                if (recipe != null && input.isEmpty())
                {
                    ItemStack stack = heldItem.copy();
                    stack.setCount(1);
                    master.inventory.set(INPUT_SLOT, stack);
                    heldItem.shrink(1);
                    return true;
                }
                if (blade.isEmpty() && heldItem.is(ATTItems.FLESHING_BLADES.get().asItem()))
                {
                    ItemStack stack = heldItem.copy();
                    stack.setCount(1);
                    master.inventory.set(BLADE_SLOT, stack);
                    heldItem.shrink(1);
                    return true;
                }
                if (player.isShiftKeyDown())
                {
                    if (!input.isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, input.copy());
                        int size = input.getCount();
                        input.shrink(size);
                        return true;
                    }
                    else if (!blade.isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, blade.copy());
                        blade.shrink(1);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void doGraphicalUpdates()
    {
        this.setChanged();
        this.markContainingBlockForUpdate(null);
    }

    @Override
    public int[] getCurrentProcessesStep()
    {
        FleshingMachineBlockEntity master = master();
        if (master != this && master != null)
            return master.getCurrentProcessesStep();
        return new int[] {processMax - process};
    }

    @Override
    public int[] getCurrentProcessesMax()
    {
        FleshingMachineBlockEntity master = master();
        if (master != this && master != null)
            return master.getCurrentProcessesMax();
        return new int[] {processMax};
    }

    private final MultiblockCapability<IItemHandler> invHandler = MultiblockCapability.make(
        this, be -> be.invHandler, FleshingMachineBlockEntity::master,
        registerCapability(new IEInventoryHandler(2, this, 0, new boolean[]{true, true}, new boolean[]{true, false})
        {
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate)
            {
                assert level != null;
                return FleshingMachineRecipe.isValidRecipeInput(level, inventory.get(slot)) ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
            }
        })
    );

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing)
    {
        if (capability == CapabilityEnergy.ENERGY && (facing == null || facing == getFacing().getClockWise()))
            return energyCap.getAndCast();
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return invHandler.getAndCast();
        return super.getCapability(capability, facing);
    }

    @Nullable
    @Override
    public NonNullList<ItemStack> getInventory()
    {
        return inventory;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack)
    {
        assert level != null;
        if (slot == 0)
            return FleshingMachineRecipe.isValidRecipeInput(level, stack);
        else
            return stack.is(ATTItems.FLESHING_BLADES.get());
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean canUseGui(Player player)
    {
        return ATTConfig.SERVER.enableFleshingMachineDebug.get();
    }

    @Override
    public FleshingMachineBlockEntity getGuiMaster()
    {
        if (!isDummy())
            return this;
        Direction dummyDir = getFacing().getCounterClockWise();
        assert level != null;
        BlockEntity be = level.getBlockEntity(worldPosition.relative(dummyDir));
        if (be instanceof FleshingMachineBlockEntity)
            return (FleshingMachineBlockEntity) be;
        return null;
    }

    @Nonnull
    @Override
    public BEContainerATT<? super FleshingMachineBlockEntity, ?> getContainerTypeATT()
    {
        return ATTContainerTypes.FLESHING_MACHINE;
    }

    private AABB renderAABB;

    @Override
    public AABB getRenderBoundingBox()
    {
        if (renderAABB == null)
            renderAABB = new AABB(getBlockPos().getX() - 1, getBlockPos().getY(), getBlockPos().getZ() - 1, getBlockPos().getX() + 2, getBlockPos().getY() + 2, getBlockPos().getZ() + 2);
        return renderAABB;
    }

    @Override
    public Property<Direction> getFacingProperty()
    {
        return IEProperties.FACING_HORIZONTAL;
    }


    @Override
    public PlacementLimitation getFacingLimitation()
    {
        return PlacementLimitation.HORIZONTAL;
    }

    @Override
    public boolean isDummy()
    {
        return getState().getValue(IEProperties.MULTIBLOCKSLAVE);
    }

    @Nullable
    @Override
    public FleshingMachineBlockEntity master()
    {
        if (!isDummy())
            return this;
        if (tempMasterBE != null)
            return (FleshingMachineBlockEntity) tempMasterBE;
        Direction dummyDir = isDummy() ? getFacing().getCounterClockWise() : getFacing().getClockWise();
        BlockPos masterPos = getBlockPos().relative(dummyDir);
        BlockEntity be = Utils.getExistingTileEntity(level, masterPos);
        return (be instanceof FleshingMachineBlockEntity) ? (FleshingMachineBlockEntity) be : null;
    }

    @Override
    public void placeDummies(BlockPlaceContext context, BlockState state)
    {
        assert level != null;
        FleshingMachineBlock.placeDummies(getBlockState(), level, worldPosition, context);
    }

    @Override
    public void breakDummies(BlockPos pos, BlockState state)
    {
        tempMasterBE = master();
        Direction dummyDir = isDummy() ? getFacing().getCounterClockWise() : getFacing().getClockWise();
        assert level != null;
        level.removeBlock(pos.relative(dummyDir), false);
    }

    @Override
    public boolean canHammerRotate(Direction side, Vec3 hit, LivingEntity entity)
    {
        return false;
    }

    @Override
    public BlockPos getModelOffset(BlockState state, @Nullable Vec3i size)
    {
        if (isDummy())
            return DUMMY_POS;
        else
            return MASTER_POS;
    }
}
