package com.hermitowo.advancedtfctech.common.container;

import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.BeamhouseLogic;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.GristMillLogic;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.PowerLoomLogic;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ThresherLogic;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

@SuppressWarnings("SameParameterValue")
public class ATTContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);

    public static final ATTMultiblockContainer<ThresherLogic.State, ThresherContainer> THRESHER = registerMultiblock(
        "thresher", ThresherContainer::makeServer, ThresherContainer::makeClient);
    public static final ATTMultiblockContainer<GristMillLogic.State, GristMillContainer> GRIST_MILL = registerMultiblock(
        "grist_mill", GristMillContainer::makeServer, GristMillContainer::makeClient);
    public static final ATTMultiblockContainer<PowerLoomLogic.State, PowerLoomContainer> POWER_LOOM = registerMultiblock(
        "power_loom", PowerLoomContainer::makeServer, PowerLoomContainer::makeClient);
    public static final ATTMultiblockContainer<BeamhouseLogic.State, BeamhouseContainer> BEAMHOUSE = registerMultiblock(
        "beamhouse", BeamhouseContainer::makeServer, BeamhouseContainer::makeClient);
    public static final ATTArgContainer<FleshingMachineBlockEntity, FleshingMachineContainer> FLESHING_MACHINE = registerArg(
        "fleshing_machine", FleshingMachineContainer::makeServer, FleshingMachineContainer::makeClient);

    private static <T, C extends IEContainerMenu> ATTArgContainer<T, C> registerArg(String name, IEMenuTypes.ArgContainerConstructor<T, C> container, IEMenuTypes.ClientContainerConstructor<C> client)
    {
        RegistryObject<MenuType<C>> typeRef = registerType(name, client);
        return new ATTArgContainer<>(typeRef, container);
    }

    public static <S extends IMultiblockState, C extends IEContainerMenu> ATTMultiblockContainer<S, C> registerMultiblock(String name, IEMenuTypes.ArgContainerConstructor<IEContainerMenu.MultiblockMenuContext<S>, C> container, IEMenuTypes.ClientContainerConstructor<C> client)
    {
        RegistryObject<MenuType<C>> typeRef = registerType(name, client);
        return new ATTMultiblockContainer<>(typeRef, container);
    }

    private static <C extends IEContainerMenu> RegistryObject<MenuType<C>> registerType(String name, IEMenuTypes.ClientContainerConstructor<C> client)
    {
        return CONTAINERS.register(
            name, () -> {
                Mutable<MenuType<C>> typeBox = new MutableObject<>();
                MenuType<C> type = new MenuType<>((id, inv) -> client.construct(typeBox.getValue(), id, inv), FeatureFlagSet.of());
                typeBox.setValue(type);
                return type;
            }
        );
    }

    public static class ATTArgContainer<T, C extends IEContainerMenu>
    {
        private final RegistryObject<MenuType<C>> type;
        private final IEMenuTypes.ArgContainerConstructor<T, C> factory;

        private ATTArgContainer(RegistryObject<MenuType<C>> type, IEMenuTypes.ArgContainerConstructor<T, C> factory)
        {
            this.type = type;
            this.factory = factory;
        }

        public C create(int windowId, Inventory playerInv, T tile)
        {
            return factory.construct(getType(), windowId, playerInv, tile);
        }

        public MenuProvider provide(T arg)
        {
            return new MenuProvider()
            {
                @Nonnull
                @Override
                public Component getDisplayName()
                {
                    return Component.empty();
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inventory, @Nonnull Player player)
                {
                    return create(containerId, inventory, arg);
                }
            };
        }

        public MenuType<C> getType()
        {
            return type.get();
        }
    }

    public static class ATTMultiblockContainer<S extends IMultiblockState, C extends IEContainerMenu> extends ATTArgContainer<IEContainerMenu.MultiblockMenuContext<S>, C>
    {
        private ATTMultiblockContainer(RegistryObject<MenuType<C>> type, IEMenuTypes.ArgContainerConstructor<IEContainerMenu.MultiblockMenuContext<S>, C> factory)
        {
            super(type, factory);
        }

        public MenuProvider provide(IMultiblockContext<S> ctx, BlockPos relativeClicked)
        {
            return provide(new IEContainerMenu.MultiblockMenuContext<>(ctx, ctx.getLevel().toAbsolute(relativeClicked)));
        }
    }
}
