package de.tubs.variantwrynn.util;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.function.BooleanSupplier;

public class Yield<T> implements Iterator<T> {
    private Supplier<T> getNext;
    private BooleanSupplier hasNext;

    public Yield(BooleanSupplier hasNext, Supplier<T> getNext) {
        this.getNext = getNext;
        this.hasNext = hasNext;
    }

    @Override
    public boolean hasNext() {
        return hasNext.getAsBoolean();
    }

    @Override
    public T next() {
        return getNext.get();
    }
}
