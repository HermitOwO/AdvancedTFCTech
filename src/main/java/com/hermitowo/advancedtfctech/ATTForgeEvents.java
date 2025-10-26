package com.hermitowo.advancedtfctech;

import java.util.ArrayList;
import java.util.List;
import com.hermitowo.advancedtfctech.common.container.ATTContainerMenu;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericDataSerializers;
import com.hermitowo.advancedtfctech.common.network.ATTMessageContainerData;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

public class ATTForgeEvents
{
    public static void init()
    {
        final IEventBus bus = NeoForge.EVENT_BUS;

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
            serverPlayer.connection.send(new ATTMessageContainerData(list));
        }
    }

    public static void onContainerClose(PlayerContainerEvent.Close event)
    {
        if (event.getContainer() instanceof ATTContainerMenu attContainer && event.getEntity() instanceof ServerPlayer serverPlayer)
            attContainer.usingPlayers.remove(serverPlayer);
    }
}
