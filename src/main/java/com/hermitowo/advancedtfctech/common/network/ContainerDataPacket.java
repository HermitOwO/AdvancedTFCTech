package com.hermitowo.advancedtfctech.common.network;

import java.util.List;
import blusunrize.immersiveengineering.common.network.PacketUtils;
import com.hermitowo.advancedtfctech.common.container.ATTContainerMenu;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericDataSerializers;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import net.dries007.tfc.client.ClientHelpers;

public class ContainerDataPacket
{
    private final List<Pair<Integer, ATTGenericDataSerializers.DataPair<?>>> synced;

    public ContainerDataPacket(List<Pair<Integer, ATTGenericDataSerializers.DataPair<?>>> synced)
    {
        this.synced = synced;
    }

    ContainerDataPacket(FriendlyByteBuf buffer)
    {
        this(PacketUtils.readList(buffer, pb -> Pair.of(pb.readVarInt(), ATTGenericDataSerializers.read(pb))));
    }

    void encode(FriendlyByteBuf buffer)
    {
        PacketUtils.writeList(buffer, synced, (pair, b) -> {
            b.writeVarInt(pair.getFirst());
            pair.getSecond().write(b);
        });
    }

    void handle(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            final Player player = ClientHelpers.getPlayer();
            if (player != null)
            {
                AbstractContainerMenu currentContainer = player.containerMenu;
                if (currentContainer instanceof ATTContainerMenu attContainer)
                    attContainer.receiveSyncATT(synced);
            }
        });
    }
}
