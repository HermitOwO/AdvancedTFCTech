package com.hermitowo.advancedtfctech.common.multiblocks.component;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IMultiblockComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import com.hermitowo.advancedtfctech.common.container.ATTContainerTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

/**
 * {@link blusunrize.immersiveengineering.common.blocks.multiblocks.component.MultiblockGui}
 */
public record ATTMultiblockGui<S extends IMultiblockState>(ATTContainerTypes.ATTMultiblockContainer<S, ?> menu) implements IMultiblockComponent<S>
{
    @Override
    public InteractionResult click(IMultiblockContext<S> ctx, BlockPos posInMultiblock, Player player, InteractionHand hand, BlockHitResult absoluteHit, boolean isClient)
    {
        if (!isClient)
            player.openMenu(menu.provide(ctx, posInMultiblock));
        return InteractionResult.SUCCESS;
    }
}
