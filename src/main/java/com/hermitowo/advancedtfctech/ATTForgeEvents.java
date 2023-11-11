package com.hermitowo.advancedtfctech;

import java.util.ArrayList;
import java.util.List;
import com.hermitowo.advancedtfctech.common.container.ATTContainerMenu;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericDataSerializers;
import com.hermitowo.advancedtfctech.common.network.ATTPacketHandler;
import com.hermitowo.advancedtfctech.common.network.ContainerDataPacket;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.PacketDistributor;

public class ATTForgeEvents
{
    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(ATTForgeEvents::onContainerOpen);
        bus.addListener(ATTForgeEvents::onContainerClose);
    }

    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        if (event.getContainer() instanceof ATTContainerMenu attContainer && event.getEntity() instanceof ServerPlayer serverPlayer)
        {
            attContainer.usingPlayers.add(serverPlayer);
            List<Pair<Integer, ATTGenericDataSerializers.DataPair<?>>> list = new ArrayList<>();
            for (int i = 0; i < attContainer.genericData.size(); i++)
                list.add(Pair.of(i, attContainer.genericData.get(i).dataPair()));
            ATTPacketHandler.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ContainerDataPacket(list));
        }
    }

    public static void onContainerClose(PlayerContainerEvent.Close event)
    {
        if (event.getContainer() instanceof ATTContainerMenu attContainer && event.getEntity() instanceof ServerPlayer serverPlayer)
            attContainer.usingPlayers.remove(serverPlayer);
    }
}
