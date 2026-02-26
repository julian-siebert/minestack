package minestack;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {
    private static final Object UNSET = new Object();

    private final AtomicReference<Object> ref = new AtomicReference<>(UNSET);

    private final Supplier<T> supplier;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Lazy<T> of(@NotNull Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        var v = ref.get();
        if (v != UNSET) return (T) v;
        var computed = supplier.get();
        ref.compareAndSet(UNSET, computed);
        return computed;
    }
}
