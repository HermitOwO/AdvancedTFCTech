package com.hermitowo.advancedtfctech.common.container.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;
import com.hermitowo.advancedtfctech.common.container.BeamhouseContainer;
import com.hermitowo.advancedtfctech.common.container.GristMillContainer;
import com.hermitowo.advancedtfctech.common.container.PowerLoomContainer;
import com.hermitowo.advancedtfctech.common.container.ThresherContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

/**
 * {@link blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers}
 */
public class ATTGenericDataSerializers
{
    private static final List<DataSerializer<?>> SERIALIZERS = new ArrayList<>();

    public static final DataSerializer<List<ThresherContainer.ProcessSlot>> THRESHER_PROCESS_SLOTS = register(
        ThresherContainer.ProcessSlot.STREAM_CODEC.apply(ByteBufCodecs.list())
    );

    public static final DataSerializer<List<GristMillContainer.ProcessSlot>> GRIST_MILL_PROCESS_SLOTS = register(
        GristMillContainer.ProcessSlot.STREAM_CODEC.apply(ByteBufCodecs.list())
    );

    public static final DataSerializer<List<PowerLoomContainer.ProcessSlot>> POWER_LOOM_PROCESS_SLOTS = register(
        PowerLoomContainer.ProcessSlot.STREAM_CODEC.apply(ByteBufCodecs.list())
    );

    public static final DataSerializer<List<BeamhouseContainer.ProcessSlot>> BEAMHOUSE_PROCESS_SLOTS = register(
        BeamhouseContainer.ProcessSlot.STREAM_CODEC.apply(ByteBufCodecs.list())
    );

    private static <T> DataSerializer<T> register(StreamCodec<? super RegistryFriendlyByteBuf, T> codec)
    {
        return register(codec, t -> t, Objects::equals);
    }

    private static <T> DataSerializer<T> register(
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec, UnaryOperator<T> copy, BiPredicate<T, T> equals
    )
    {
        DataSerializer<T> serializer = new DataSerializer<>(codec, copy, equals, SERIALIZERS.size());
        SERIALIZERS.add(serializer);
        return serializer;
    }

    public record DataSerializer<T>(
        StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
        UnaryOperator<T> copy,
        BiPredicate<T, T> equals,
        int id
    )
    {
        private DataPair<T> read(RegistryFriendlyByteBuf from)
        {
            return new DataPair<>(this, codec.decode(from));
        }
    }

    public record DataPair<T>(DataSerializer<T> serializer, T data)
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, DataPair<?>> CODEC = new StreamCodec<>()
        {
            @Override
            public @NotNull DataPair<?> decode(RegistryFriendlyByteBuf buffer)
            {
                DataSerializer<?> serializer = SERIALIZERS.get(buffer.readVarInt());
                return serializer.read(buffer);
            }

            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buffer, DataPair<?> data)
            {
                data.write(buffer);
            }
        };

        private void write(RegistryFriendlyByteBuf to)
        {
            to.writeVarInt(serializer.id());
            serializer.codec.encode(to, data);
        }
    }
}
