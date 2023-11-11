package com.hermitowo.advancedtfctech.common.container;

import java.util.ArrayList;
import java.util.List;
import blusunrize.immersiveengineering.common.gui.IEContainerMenu;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericContainerData;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericDataSerializers;
import com.hermitowo.advancedtfctech.common.network.ATTPacketHandler;
import com.hermitowo.advancedtfctech.common.network.ContainerDataPacket;
import com.mojang.datafixers.util.Pair;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public abstract class ATTContainerMenu extends IEContainerMenu
{
    public final List<ATTGenericContainerData<?>> genericData = new ArrayList<>();
    public List<ServerPlayer> usingPlayers = new ArrayList<>();

    protected ATTContainerMenu(MenuContext ctx)
    {
        super(ctx);
    }

    public void addGenericData(ATTGenericContainerData<?> newData)
    {
        genericData.add(newData);
    }

    @Override
    public void broadcastChanges()
    {
        super.broadcastChanges();
        List<Pair<Integer, ATTGenericDataSerializers.DataPair<?>>> toSync = new ArrayList<>();
        for (int i = 0; i < genericData.size(); i++)
        {
            ATTGenericContainerData<?> data = genericData.get(i);
            if (data.needsUpdate())
                toSync.add(Pair.of(i, data.dataPair()));
        }
        if (!toSync.isEmpty())
            for (ServerPlayer player : usingPlayers)
                ATTPacketHandler.send(PacketDistributor.PLAYER.with(() -> player), new ContainerDataPacket(toSync));
    }

    public void receiveSyncATT(List<Pair<Integer, ATTGenericDataSerializers.DataPair<?>>> synced)
    {
        for (Pair<Integer, ATTGenericDataSerializers.DataPair<?>> syncElement : synced)
            genericData.get(syncElement.getFirst()).processSync(syncElement.getSecond().data());
    }
}
