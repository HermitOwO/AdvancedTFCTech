package com.hermitowo.advancedtfctech.common.container;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.register.IEContainerTypes;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.RegistryObject;

public interface ATTContainerProvider<T extends BlockEntity & ATTContainerProvider<T>> extends IEBlockInterfaces.IInteractionObjectIE<T>
{
    default IEContainerTypes.BEContainer<? super T, ?> getContainerType()
    {
        return null;
    }

    @Nonnull
    BEContainerATT<? super T, ?> getContainerTypeATT();

    @Nonnull
    @Override
    default AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity)
    {
        T master = getGuiMaster();
        Preconditions.checkNotNull(master);
        BEContainerATT<? super T, ?> type = getContainerTypeATT();
        return type.create(id, playerInventory, master);
    }

    record BEContainerATT<T extends BlockEntity, C extends IEBaseContainer<? super T>>(RegistryObject<MenuType<C>> type, IEContainerTypes.BEContainerConstructor<T, C> factory)
    {
        public C create(int windowId, Inventory playerInv, T tile)
        {
            return factory.construct(getType(), windowId, playerInv, tile);
        }

        public MenuType<C> getType()
        {
            return type.get();
        }
    }
}
