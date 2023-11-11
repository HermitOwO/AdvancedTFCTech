package com.hermitowo.advancedtfctech.common.container.sync;

import java.util.function.Consumer;
import java.util.function.Supplier;
import blusunrize.immersiveengineering.common.gui.sync.GetterAndSetter;

/**
 * {@link blusunrize.immersiveengineering.common.gui.sync.GenericContainerData}
 */
@SuppressWarnings("unchecked")
public class ATTGenericContainerData<T>
{
    private final ATTGenericDataSerializers.DataSerializer<T> serializer;
    private final Supplier<T> get;
    private final Consumer<T> set;
    private T current;

    public ATTGenericContainerData(ATTGenericDataSerializers.DataSerializer<T> serializer, Supplier<T> get, Consumer<T> set)
    {
        this.serializer = serializer;
        this.get = get;
        this.set = set;
    }

    public ATTGenericContainerData(ATTGenericDataSerializers.DataSerializer<T> serializer, GetterAndSetter<T> io)
    {
        this.serializer = serializer;
        this.get = io.getter();
        this.set = io.setter();
    }

    public boolean needsUpdate()
    {
        T newValue = get.get();
        if (newValue == null && current == null)
            return false;
        if (current != null && newValue != null && serializer.equals().test(current, newValue))
            return false;
        current = serializer.copy().apply(newValue);
        return true;
    }

    public void processSync(Object receivedData)
    {
        current = (T) receivedData;
        set.accept(serializer.copy().apply(current));
    }

    public ATTGenericDataSerializers.DataPair<T> dataPair()
    {
        return new ATTGenericDataSerializers.DataPair<>(serializer, current);
    }
}
