package com.hermitowo.advancedtfctech.common.container.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import com.hermitowo.advancedtfctech.common.container.BeamhouseContainer;
import com.hermitowo.advancedtfctech.common.container.GristMillContainer;
import com.hermitowo.advancedtfctech.common.container.PowerLoomContainer;
import com.hermitowo.advancedtfctech.common.container.ThresherContainer;
import net.minecraft.network.FriendlyByteBuf;

/**
 * {@link blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers}
 */
public class ATTGenericDataSerializers
{
    private static final List<DataSerializer<?>> SERIALIZERS = new ArrayList<>();

    public static final DataSerializer<List<ThresherContainer.ProcessSlot>> THRESHER_PROCESS_SLOTS = register(
        buffer -> buffer.readList(ThresherContainer.ProcessSlot::from), (buffer, list) -> buffer.writeCollection(list, ThresherContainer.ProcessSlot::writeTo)
    );

    public static final DataSerializer<List<GristMillContainer.ProcessSlot>> GRIST_MILL_PROCESS_SLOTS = register(
        buffer -> buffer.readList(GristMillContainer.ProcessSlot::from), (buffer, list) -> buffer.writeCollection(list, GristMillContainer.ProcessSlot::writeTo)
    );

    public static final DataSerializer<List<PowerLoomContainer.ProcessSlot>> POWER_LOOM_PROCESS_SLOTS = register(
        buffer -> buffer.readList(PowerLoomContainer.ProcessSlot::from), (buffer, list) -> buffer.writeCollection(list, PowerLoomContainer.ProcessSlot::writeTo)
    );

    public static final DataSerializer<List<BeamhouseContainer.ProcessSlot>> BEAMHOUSE_PROCESS_SLOTS = register(
        buffer -> buffer.readList(BeamhouseContainer.ProcessSlot::from), (buffer, list) -> buffer.writeCollection(list, BeamhouseContainer.ProcessSlot::writeTo)
    );

    private static <T> DataSerializer<T> register(Function<FriendlyByteBuf, T> read, BiConsumer<FriendlyByteBuf, T> write)
    {
        return register(read, write, t -> t, Objects::equals);
    }

    private static <T> DataSerializer<T> register(Function<FriendlyByteBuf, T> read, BiConsumer<FriendlyByteBuf, T> write, UnaryOperator<T> copy, BiPredicate<T, T> equals)
    {
        DataSerializer<T> serializer = new DataSerializer<>(read, write, copy, equals, SERIALIZERS.size());
        SERIALIZERS.add(serializer);
        return serializer;
    }

    public static DataPair<?> read(FriendlyByteBuf buffer)
    {
        DataSerializer<?> serializer = SERIALIZERS.get(buffer.readVarInt());
        return serializer.read(buffer);
    }

    public record DataSerializer<T>(Function<FriendlyByteBuf, T> read, BiConsumer<FriendlyByteBuf, T> write, UnaryOperator<T> copy, BiPredicate<T, T> equals, int id)
    {
        public DataPair<T> read(FriendlyByteBuf from)
        {
            return new DataPair<>(this, read().apply(from));
        }
    }

    public record DataPair<T>(DataSerializer<T> serializer, T data)
    {
        public void write(FriendlyByteBuf to)
        {
            to.writeVarInt(serializer.id());
            serializer.write().accept(to, data);
        }
    }
}
