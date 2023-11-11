package com.hermitowo.advancedtfctech.common.container;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ATTContainerProvider<T extends BlockEntity & ATTContainerProvider<T>> extends IEBlockInterfaces.IInteractionObjectIE<T>
{
    default IEMenuTypes.ArgContainer<? super T, ?> getContainerType()
    {
        return null;
    }

    @Nonnull
    ATTContainerTypes.ATTArgContainer<? super T, ?> getContainerTypeATT();

    @Nonnull
    @Override
    default AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity)
    {
        T master = getGuiMaster();
        Preconditions.checkNotNull(master);
        ATTContainerTypes.ATTArgContainer<? super T, ?> type = getContainerTypeATT();
        return type.create(id, playerInventory, master);
    }
}
