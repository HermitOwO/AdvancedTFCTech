package com.hermitowo.advancedtfctech.util;

import java.util.function.Supplier;
import com.google.common.base.Preconditions;

public class ModifiableSupplier<T> implements Supplier<T>
{
    private T value;

    public static <T> ModifiableSupplier<T> of()
    {
        return new ModifiableSupplier<>();
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    @Override
    public T get()
    {
        return Preconditions.checkNotNull(value);
    }
}
