package com.hermitowo.advancedtfctech.common.network;

import java.util.List;
import com.hermitowo.advancedtfctech.common.container.ATTContainerMenu;
import com.hermitowo.advancedtfctech.common.container.sync.ATTGenericDataSerializers;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.dries007.tfc.client.ClientHelpers;

public record ATTMessageContainerData(List<Pair<Integer, ATTGenericDataSerializers.DataPair<?>>> synced) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ATTMessageContainerData> TYPE = ATTPacketHandler.type("att_container_data");
    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<Integer, ATTGenericDataSerializers.DataPair<?>>> PAIR_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, Pair::getFirst,
        ATTGenericDataSerializers.DataPair.CODEC, Pair::getSecond,
        Pair::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ATTMessageContainerData> CODEC = PAIR_CODEC
        .apply(ByteBufCodecs.list())
        .map(ATTMessageContainerData::new, ATTMessageContainerData::synced);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle()
    {
        final Player player = ClientHelpers.getPlayer();
        if (player != null)
        {
            AbstractContainerMenu currentContainer = player.containerMenu;
            if (currentContainer instanceof ATTContainerMenu attContainer)
                attContainer.receiveSyncATT(synced);
        }
    }
}
