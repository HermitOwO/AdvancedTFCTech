package com.hermitowo.advancedtfctech.common.container;

import java.util.function.Supplier;
import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.register.IEContainerTypes;
import com.hermitowo.advancedtfctech.common.blockentities.BeamhouseBlockEntity;
import com.hermitowo.advancedtfctech.common.blockentities.FleshingMachineBlockEntity;
import com.hermitowo.advancedtfctech.common.blockentities.GristMillBlockEntity;
import com.hermitowo.advancedtfctech.common.blockentities.PowerLoomBlockEntity;
import com.hermitowo.advancedtfctech.common.blockentities.ThresherBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);

    public static final ATTContainerProvider.BEContainerATT<ThresherBlockEntity, ThresherContainer> THRESHER = register("thresher", ThresherContainer::new);
    public static final ATTContainerProvider.BEContainerATT<GristMillBlockEntity, GristMillContainer> GRIST_MILL = register("grist_mill", GristMillContainer::new);
    public static final ATTContainerProvider.BEContainerATT<PowerLoomBlockEntity, PowerLoomContainer> POWER_LOOM = register("power_loom", PowerLoomContainer::new);
    public static final ATTContainerProvider.BEContainerATT<BeamhouseBlockEntity, BeamhouseContainer> BEAMHOUSE = register("beamhouse", BeamhouseContainer::new);
    public static final ATTContainerProvider.BEContainerATT<FleshingMachineBlockEntity, FleshingMachineContainer> FLESHING_MACHINE = register("fleshing_machine", FleshingMachineContainer::new);

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenu(String name, Supplier<MenuType<T>> factory)
    {
        return CONTAINERS.register(name, factory);
    }


    public static <T extends BlockEntity, C extends IEBaseContainer<? super T>> ATTContainerProvider.BEContainerATT<T, C> register(String name, IEContainerTypes.BEContainerConstructor<T, C> container)
    {
        RegistryObject<MenuType<C>> typeRef = registerMenu(name, () -> {
            Mutable<MenuType<C>> typeBox = new MutableObject<>();
            MenuType<C> type = new MenuType<>((IContainerFactory<C>) (windowId, inv, data) -> {
                Level world = ImmersiveEngineering.proxy.getClientWorld();
                BlockPos pos = data.readBlockPos();
                BlockEntity blockentity = world.getBlockEntity(pos);
                return container.construct(typeBox.getValue(), windowId, inv, (T) blockentity);
            });
            typeBox.setValue(type);
            return type;
        });
        return new ATTContainerProvider.BEContainerATT<>(typeRef, container);
    }
}
