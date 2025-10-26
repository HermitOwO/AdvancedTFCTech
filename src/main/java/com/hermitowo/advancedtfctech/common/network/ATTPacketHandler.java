package com.hermitowo.advancedtfctech.common.network;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ATTPacketHandler
{
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> type(String id)
    {
        return new CustomPacketPayload.Type<>(AdvancedTFCTech.rl(id));
    }

    public static void setup(RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar register = event.registrar(ModList.get().getModFileById(AdvancedTFCTech.MOD_ID).versionString());

        // Server -> Client
        register.playToClient(ATTMessageContainerData.TYPE, ATTMessageContainerData.CODEC, onClient(ATTMessageContainerData::handle));

        // Client -> Server
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> onClient(Consumer<T> handler)
    {
        return (payload, context) -> context.enqueueWork(() -> handler.accept(payload));
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> onServer(BiConsumer<T, ServerPlayer> handler)
    {
        return (payload, context) -> context.enqueueWork(() -> handler.accept(payload, (ServerPlayer) context.player()));
    }
}
