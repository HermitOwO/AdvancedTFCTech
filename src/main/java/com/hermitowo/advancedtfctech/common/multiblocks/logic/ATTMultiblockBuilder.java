package com.hermitowo.advancedtfctech.common.multiblocks.logic;

import blusunrize.immersiveengineering.api.multiblocks.blocks.MultiblockRegistrationBuilder;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.ComparatorManager;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.google.common.base.Preconditions;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import com.hermitowo.advancedtfctech.common.multiblocks.component.ATTMultiblockGui;
import net.minecraft.core.BlockPos;

/**
 * {@link blusunrize.immersiveengineering.common.blocks.multiblocks.logic.IEMultiblockBuilder}
 */
@SuppressWarnings("unused")
public class ATTMultiblockBuilder<S extends IMultiblockState> extends MultiblockRegistrationBuilder<S, ATTMultiblockBuilder<S>>
{
    public ATTMultiblockBuilder(IMultiblockLogic<S> logic, String name)
    {
        super(logic, AdvancedTFCTech.rl(name));
    }

    public ATTMultiblockBuilder<S> gui(ATTContainerTypes.ATTMultiblockContainer<S, ?> menu)
    {
        return component(new ATTMultiblockGui<>(menu));
    }

    public ATTMultiblockBuilder<S> redstoneNoComputer(IMultiblockComponent.StateWrapper<S, RedstoneControl.RSState> getState, BlockPos... positions)
    {
        redstoneAware();
        return selfWrappingComponent(new RedstoneControl<>(getState, false, positions));
    }

    public ATTMultiblockBuilder<S> redstone(IMultiblockComponent.StateWrapper<S, RedstoneControl.RSState> getState, BlockPos... positions)
    {
        redstoneAware();
        return selfWrappingComponent(new RedstoneControl<>(getState, positions));
    }

    public ATTMultiblockBuilder<S> comparator(ComparatorManager<S> comparator)
    {
        withComparator();
        return super.selfWrappingComponent(comparator);
    }

    @Override
    public <CS, C extends IMultiblockComponent<CS> & IMultiblockComponent.StateWrapper<S, CS>>
    ATTMultiblockBuilder<S> selfWrappingComponent(C extraComponent)
    {
        Preconditions.checkArgument(!(extraComponent instanceof ComparatorManager<?>));
        return super.selfWrappingComponent(extraComponent);
    }

    @Override
    protected ATTMultiblockBuilder<S> self()
    {
        return this;
    }
}
